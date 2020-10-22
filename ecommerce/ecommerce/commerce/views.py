import json
import random
from datetime import datetime
from pprint import pprint

import requests
from django.contrib.auth import authenticate
from django.contrib.auth.hashers import make_password
from django.http import JsonResponse
from django.shortcuts import render
from django.template.response import TemplateResponse
from django.views.decorators.csrf import csrf_exempt
from requests.auth import HTTPBasicAuth
from rest_framework import viewsets, permissions, status
from rest_framework.authtoken.models import Token
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny
from rest_framework.response import Response
from rest_framework.status import HTTP_400_BAD_REQUEST, HTTP_404_NOT_FOUND, HTTP_200_OK

from commerce.models import *
from commerce.mpesa_credentials import MpesaAccessToken, LipanaMpesaPpassword
from commerce.serializers import *
from commerce.utils import SentimentAnalyzer


def getAccessToken(request):
    consumer_key = 'cHnkwYIgBbrxlgBoneczmIJFXVm0oHky'
    consumer_secret = '2nHEyWSD4VjpNh2g'
    api_URL = 'https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials'
    r = requests.get(api_URL, auth=HTTPBasicAuth(consumer_key, consumer_secret))
    mpesa_access_token = json.loads(r.text)
    validated_mpesa_access_token = mpesa_access_token['access_token']
    return HttpResponse(validated_mpesa_access_token)


@csrf_exempt
@api_view(["POST"])
@permission_classes((AllowAny,))
def get_access_token(request):
    username = request.data.get("consumer_key")
    password = request.data.get("consumer_password")
    if username is None or password is None:
        return Response('Please provide both consumer key and consumer password', status=HTTP_400_BAD_REQUEST)
    user = authenticate(username=username, password=password)

    if not user:
        context = {
            'error': 'Invalid Credentials',
        }
        return Response('Invalid Credentials', status=HTTP_400_BAD_REQUEST)
    token, _ = Token.objects.get_or_create(user=user)

    buyer = Buyer.objects.get(user_ptr_id=user.id)
    s = UserSerializer(buyer, many=False)
    context = {
        'token': token.key,
    }
    print(context)
    return Response(context, status=HTTP_200_OK)


@csrf_exempt
@api_view(["POST"])
@permission_classes((AllowAny,))
def buyer_sign_up(request):
    username = request.data.get("username", "")
    password = request.data.get("password", "")
    first_name = request.data.get("first_name", "")
    last_name = request.data.get("last_name", "")
    phone_number = request.data.get("phone_number", "")
    if not username and not password and not first_name and not last_name:
        return Response(
            data={
                "response": "username, password and email is required to register a user"
            },
            status=status.HTTP_400_BAD_REQUEST
        )
    buyer = Buyer.objects.create(
        username=username,
        password=make_password(password),
        email=username,
        first_name=first_name,
        last_name=last_name,
        is_staff=True,
        phone_number=phone_number
    )
    user = authenticate(username=username, password=password)
    token, _ = Token.objects.get_or_create(user=user)
    context = {
        'token': token.key
    }
    return Response(context, status=status.HTTP_201_CREATED)


class UserViewSet(viewsets.ModelViewSet):
    queryset = Buyer.objects.all().order_by('-date_joined')
    serializer_class = UserSerializer
    permission_classes = [permissions.AllowAny]


class ProductViewSet(viewsets.ModelViewSet):
    queryset = Product.objects.filter(low_stock__gt=0)
    serializer_class = ProductSerializer
    permission_classes = [permissions.AllowAny]


class ProductCategoryViewSet(viewsets.ModelViewSet):
    queryset = ProductCategory.objects.all()
    serializer_class = CategorySerializer
    permission_classes = [permissions.AllowAny]


class PaymentViewSet(viewsets.ModelViewSet):
    queryset = Payment.objects.all()
    serializer_class = PaymentSerializer
    permission_classes = [permissions.AllowAny]


class AddressViewSet(viewsets.ModelViewSet):
    queryset = Address.objects.all()
    serializer_class = AddressSerializer
    permission_classes = [permissions.AllowAny]


class OrderViewSet(viewsets.ModelViewSet):
    serializer_class = OrderSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        buyer = Buyer.objects.filter(user_ptr_id=self.request.user.id).first()
        return Order.objects.filter(buyer_id=buyer.id)


class OrderItemViewSet(viewsets.ModelViewSet):
    serializer_class = OrderItemSerializer
    permission_classes = [permissions.AllowAny]

    def get_queryset(self):
        return OrderItem.objects.filter(order_id=self.kwargs['order_id'])


@csrf_exempt
@api_view(["GET"])
@permission_classes((AllowAny,))
def product_comments(request, product_id):
    comments = ProductReview.objects.filter(product_id=product_id)
    s = ProductReviewSerializer(comments, many=True)

    # context = {
    #     'comments': ,
    # }
    return Response(s.data, status=status.HTTP_200_OK)


@api_view(["GET"])
@permission_classes((AllowAny,))
def add_to_cart(request, product_id):
    token = Token.objects.get(key=request.META.get('HTTP_AUTHORIZATION').split()[1])
    print(token)
    buyer = Buyer.objects.filter(user_ptr_id=token.user_id).first()
    order = Order.objects.filter(buyer_id=buyer.id, ordered=False).first()
    if order is not None:
        print(order)
        order_item = OrderItem.objects.filter(order_id=order.id, product_id=product_id).first()
        if order_item is not None:
            print(order_item)
            old_quantity = order_item.quantity
            new_quantity = old_quantity + 1
            order_item.quantity = new_quantity
            order_item.save()
            o = OOrderItemSerializer(order_item, many=False)
            return Response(o.data, status=status.HTTP_200_OK)
        else:
            order_item = OrderItem.objects.create(
                order=order,
                quantity=1,
                product_id=product_id
            )
            o = OOrderItemSerializer(order_item, many=False)
            return Response(o.data, status=status.HTTP_200_OK)
    else:
        order = Order.objects.create(
            buyer_id=buyer.id,
            unique_ref=random.randint(1000, 100000)
        )
        order_item = OrderItem.objects.create(
                order=order,
                quantity=1,
                product_id=product_id
        )
        o = OOrderItemSerializer(order_item, many=False)
        return Response(o.data, status=status.HTTP_200_OK)


@api_view(["GET"])
@permission_classes((AllowAny,))
def get_cart(request):

    token = Token.objects.get(key=request.META.get('HTTP_AUTHORIZATION').split()[1])
    print(token)
    buyer = Buyer.objects.filter(user_ptr_id=token.user_id).first()
    order = Order.objects.filter(buyer_id=buyer.id, ordered=False).first()
    if order is not None:
        pp = PProductSerializer(order.products, many=True)
        print(pp.data)
        return Response(pp.data, status=status.HTTP_200_OK)
    else:
        products = []
        return Response(products, status=status.HTTP_200_OK)


@api_view(["GET"])
@permission_classes((AllowAny,))
def get_profile(request):
    buyer = Buyer.objects.filter(id='').first()
    b = UserSerializer(buyer, many=False)
    return Response(b.data, status=status.HTTP_200_OK)


@api_view(["GET"])
@permission_classes((AllowAny,))
def get_orders(request):
    token = Token.objects.get(key=request.META.get('HTTP_AUTHORIZATION').split()[1])
    buyer = Buyer.objects.filter(user_ptr_id=token.user_id).first()
    orders = Order.objects.filter(buyer_id=buyer.id)
    o = OrderSerializer(orders, many=True)

    return Response(o.data, status=status.HTTP_200_OK)


@api_view(["GET"])
@permission_classes((AllowAny,))
def order_products(request, order_id):
    order = Order.objects.get(id=order_id)
    p = PProductSerializer(order.products, many=True)
    pprint(p.data)
    return Response(p.data, status=status.HTTP_200_OK)


@api_view(["GET"])
@permission_classes((AllowAny,))
def mark_as_delivered(request, order_id):
    order = Order.objects.get(id=order_id)
    order.delivered = True
    order.save()
    o = OrderSerializer(order, many=False)
    return Response(o.data, status=status.HTTP_200_OK)


@api_view(["GET"])
@permission_classes((AllowAny,))
def get_profile(request):
    token = Token.objects.get(key=request.META.get('HTTP_AUTHORIZATION').split()[1])
    print(token)
    buyer = Buyer.objects.filter(user_ptr_id=token.user_id).first()
    b = UUserSerializer(buyer, many=False)
    return Response(b.data, status=HTTP_200_OK)


@api_view(["POST"])
@permission_classes((AllowAny,))
def update_profile(request):
    token = Token.objects.get(key=request.META.get('HTTP_AUTHORIZATION').split()[1])
    print(token)
    buyer = Buyer.objects.filter(user_ptr_id=token.user_id).first()

    buyer.first_name = request.POST["first_name"]
    buyer.last_name = request.POST["last_name"]
    buyer.email = request.POST['email']

    buyer.save()
    context = {
        "response": "success"
    }
    b = UUserSerializer(buyer, many=False)
    return Response(b.data, status=status.HTTP_200_OK)


def lipa_na_mpesa_online(buyer, order):
    access_token = MpesaAccessToken.validated_mpesa_access_token
    api_url = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest"
    headers = {"Authorization": "Bearer %s" % access_token}
    request = {
        "BusinessShortCode": LipanaMpesaPpassword.Business_short_code,
        "Password": LipanaMpesaPpassword.decode_password,
        "Timestamp": LipanaMpesaPpassword.lipa_time,
        "TransactionType": "CustomerPayBillOnline",
        "Amount": 1,
        "PartyA": 254111979693,
        "PartyB": LipanaMpesaPpassword.Business_short_code,
        "PhoneNumber": 254111979693,
        "CallBackURL": "https://b9e932f378f5.ngrok.io/api/v1/confirmation",
        "AccountReference": str(order.unique_ref),
        "TransactionDesc": "Payment"
    }
    response = requests.post(api_url, json=request, headers=headers)
    return json.loads(response.text)


@api_view(["POST"])
@permission_classes((AllowAny,))
def change_password(request):
    token = Token.objects.get(key=request.META.get('HTTP_AUTHORIZATION').split()[1])
    buyer = Buyer.objects.filter(user_ptr_id=token.user_id).first()

    buyer.password = make_password(request.POST['password'])
    buyer.save()
    context = {
        "response": "success"
    }
    b = UUserSerializer(buyer, many=False)
    return Response(b.data, status=status.HTTP_200_OK)


@api_view(["GET"])
@permission_classes((AllowAny,))
def checkout(request):
    token = Token.objects.get(key=request.META.get('HTTP_AUTHORIZATION').split()[1])
    buyer = Buyer.objects.filter(user_ptr_id=token.user_id).first()
    order = Order.objects.filter(buyer=buyer, ordered=False).first()
    response  = lipa_na_mpesa_online(buyer=buyer, order=order)
    print(response)
    if response:
        payment = Payment.objects.create(
            first_name=buyer.first_name,
            last_name=buyer.last_name,
            phone_number=buyer.phone_number,
            order_number=order.reference
        )
        order.payment = payment
        order.ordered = True
        order.ordered_date = datetime.now()
        order.payment_status = True
        order.save()
    return Response(response, status=status.HTTP_200_OK)


@csrf_exempt
def query_mpesa(request):
    access_token = MpesaAccessToken.validated_mpesa_access_token
    url = "https://sandbox.safaricom.co.ke/mpesa/stkpushquery/v1/query"
    headers = {"Authorization": "Bearer %s" % access_token}
    body = {
        "BusinessShortCode": LipanaMpesaPpassword.Business_short_code,
        "Password": LipanaMpesaPpassword.decode_password,
        "Timestamp": LipanaMpesaPpassword.lipa_time,
        "CheckoutRequestID": "ws_CO_150920201043501347"
    }
    response = requests.post(url=url, json=body, headers=headers)
    print(json.loads(response.text))
    return Response(json.loads(response.text), status=HTTP_200_OK)


@csrf_exempt
def confirmation(request):
    mpesa_body = request.body.decode('utf-8')
    mpesa_payment = json.loads(mpesa_body)
    print(mpesa_payment)
    first_name = mpesa_payment['FirstName'],
    last_name = mpesa_payment['LastName'],
    reference = mpesa_payment['BillRefNumber'],
    phone_number = mpesa_payment['MSISDN'],
    payment = Payment.objects.create(
        first_name=first_name,
        last_name=last_name,
        phone_number=phone_number,
        order_number=reference
    )

    order = Order.objects.filter(unique_ref=reference).first()
    order.payment = payment
    order.save()
    context = {
        'response': 'success'
    }
    return Response(context, status=HTTP_200_OK)


@api_view(["POST"])
@permission_classes((AllowAny,))
def review_product(request, order_id, product_id):
    product = Product.objects.get(id=product_id)
    review = ProductReview.objects.filter(product_id=product_id, order_id=order_id).first()
    token = Token.objects.get(key=request.META.get('HTTP_AUTHORIZATION').split()[1])
    buyer = Buyer.objects.filter(user_ptr_id=token.user_id).first()

    if review is not None:
        review.comment = request.POST["comment"]
        review.rating = request.POST["rating"]
        review.save()
        r = ProductReviewSerializer(review, many=False)
        return Response(r.data, status=HTTP_200_OK)
    else:
        review = ProductReview.objects.create(
            product=product,
            buyer=buyer,
            verified_purchase=True,
            comment=request.POST["comment"],
            rating=request.POST["rating"],
            order_id_id=order_id
        )
        r = ProductReviewSerializer(review, many=False)
        return Response(r.data, status=HTTP_200_OK)


@api_view(["GET"])
@permission_classes((AllowAny,))
def get_order(request, order_id):
    order = Order.objects.get(id=order_id)
    o = OrderSerializer(order, many=False)
    return Response(o.data, status=HTTP_200_OK)


@api_view(["GET"])
@permission_classes((AllowAny,))
def get_review(request, order_id, product_id):
    review = ProductReview.objects.filter(product_id=product_id, order_id=order_id).first()
    if review is not None:
        r = ProductReviewSerializer(review, many=False)
        return Response(r.data, status=status.HTTP_200_OK)
    else:
        return Response("No Review Found", status=status.HTTP_200_OK)


@api_view(["GET"])
@permission_classes((AllowAny,))
def get_order_review_status(request, order_id):
    token = Token.objects.get(key=request.META.get('HTTP_AUTHORIZATION').split()[1])
    buyer = Buyer.objects.filter(user_ptr_id=token.user_id).first()
    order = Order.objects.get(id=order_id)
    products = order.products
    product_ids = []
    for product in products:
        product_ids.append(product.id)
    if ProductReview.objects.filter(id__in=product_ids, buyer=buyer).count() > 0:
        context = {
            "response": "Reviewed"
        }
        return Response(context, status=HTTP_200_OK)
    else:
        context = {
            'response': "Pending Review"
        }
        return Response(context, HTTP_200_OK)


@api_view(["GET"])
@permission_classes((AllowAny,))
def get_in_cart(request, product_id):
    token = Token.objects.get(key=request.META.get('HTTP_AUTHORIZATION').split()[1])
    buyer = Buyer.objects.filter(user_ptr_id=token.user_id).first()
    order = Order.objects.filter(buyer=buyer, ordered=False).first()
    order_item = OrderItem.objects.filter(order_id=order.id, product_id=product_id).first()

    o = OOrderItemSerializer(order_item, many=False)
    return Response(o.data, status=HTTP_200_OK)


@csrf_exempt
def test_sentiment_analyzer(request):
    response = SentimentAnalyzer()
    print(response)
    return JsonResponse(response)