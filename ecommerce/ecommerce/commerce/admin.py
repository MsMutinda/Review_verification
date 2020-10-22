from django.contrib import admin

from commerce.models import *

admin.site.register(Product)
admin.site.register(ProductReview)
admin.site.register(Order)
admin.site.register(OrderItem)
admin.site.register(ProductCategory)
