from django.conf.urls import url
from django.urls import path, include
from rest_framework import routers
from commerce import views
from commerce.views import OrderViewSet, OrderItemViewSet

router = routers.DefaultRouter()
router.register(r'users', views.UserViewSet)
router.register(r'products', views.ProductViewSet)
router.register(r'categories', views.ProductCategoryViewSet)
router.register(r'payments', views.PaymentViewSet)
router.register(r'address', views.AddressViewSet)
router.register(r'orders', views.OrderViewSet, basename='orders')
# router.register(r'order_items', views.OrderItemViewSet, basename='order_items')
# order_items = OrderItemViewSet.as_view({
#     'get': 'retrieve',
#     'put': 'update',
#     'patch': 'partial_update',
#     'delete': 'destroy'
# })
urlpatterns = [
    path('', include(router.urls)),
    path('api-auth/', include('rest_framework.urls')),
    # path('/order/items/<int:order_id>', order_items, name='order_items'),
    path('access/token', views.getAccessToken, name='get_mpesa_access_token'),
    path('online/lipa', views.lipa_na_mpesa_online, name='lipa_na_mpesa'),

    path('product/reviews/<int:product_id>/', views.product_comments),
    path('product/review/<int:order_id>/<int:product_id>', views.get_review),
    path('submit/rating/<int:order_id>/<int:product_id>', views.review_product),

    path('login', views.get_access_token),
    path('sign/up', views.buyer_sign_up),
    path('cart', views.get_cart),
    path('add/to/cart/<int:product_id>', views.add_to_cart),

    path('order/products/<int:order_id>', views.order_products),
    path('order/<int:order_id>', views.get_order),
    path('order/ordered/<int:order_id>', views.mark_as_delivered),
    path('order/review/status/<int:order_id>', views.get_order_review_status),

    path('profile', views.get_profile),
    path('update/profile', views.update_profile),
    path('change/password', views.change_password),

    path('checkout', views.checkout),

    path('get/value/in/cart/<int:product_id>', views.get_in_cart),
    path('confirmation', views.confirmation),
    path('query_mpesa', views.query_mpesa),


    #test
    path('test_sentiment_analyzer', views.test_sentiment_analyzer)
]