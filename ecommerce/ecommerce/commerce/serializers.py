from rest_framework import serializers
from commerce.models import *


class UserSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Buyer
        fields = ['url', 'username', 'email', 'first_name', 'last_name', 'phone_number', 'password']


class UUserSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Buyer
        fields = ['username', 'email', 'first_name', 'last_name', 'phone_number', 'password']


class ProductSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Product
        fields = ['url', 'id', 'name', 'short_description', 'description', 'unit_price', 'stock', 'low_stock',
                  'category', 'image1', 'image2', 'image3', 'image4']


class PProductSerializer(serializers.ModelSerializer):
    class Meta:
        model = Product
        fields = ['id', 'name', 'short_description', 'description', 'unit_price', 'stock', 'low_stock',
                  'category', 'image1', 'image2', 'image3', 'image4']


class CategorySerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = ProductCategory
        fields = ['url','name', 'description']


class PaymentSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Payment
        fields = ['url','first_name', 'last_name', 'order_number', 'phone_number']


class AddressSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Address
        fields = ['url','buyer', 'street_address', 'apartment_address', 'country', 'zip']


class OrderSerializer(serializers.ModelSerializer):
    class Meta:
        model = Order
        fields = ['id', 'buyer', 'ordered', 'ordered_date', 'delivered', 'billing_address',
                  'payment', 'unique_ref', 'product_count', 'total_amount', 'review_status']


class OrderItemSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = OrderItem
        fields = ['url', 'order', 'product', 'quantity']


class OOrderItemSerializer(serializers.ModelSerializer):
    class Meta:
        model = OrderItem
        fields = ['order', 'product', 'quantity']


class ProductReviewSerializer(serializers.ModelSerializer):
    # buyer_name = serializers.SerializerMethodField()
    class Meta:
        model = ProductReview
        fields = ['product', 'buyer', 'verified_purchase', 'comment', 'rating', 'image', 'buyer_name', 'review_verified']