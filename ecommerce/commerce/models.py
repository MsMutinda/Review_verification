from django.contrib.auth import get_user_model
from django.db import models


class Buyer(get_user_model()):
    phone_number = models.CharField(max_length=12)


class ProductCategory(models.Model):
    name = models.CharField(max_length=250)
    description = models.CharField(max_length=250)


# class Variant(models.Model):
#     name = models.CharField(max_length=250) # eg color, size
#
#
# class VariantOption(models.Model):
#     name = models.CharField(max_length=250) # color blue, black
#     variant = models.ForeignKey(Variant, on_delete=models.CASCADE)


class Product(models.Model):
    name = models.CharField(max_length=250)
    short_description = models.TextField()
    description = models.TextField()
    unit_price = models.DecimalField(max_digits=12, decimal_places=2)
    stock = models.IntegerField()
    low_stock = models.IntegerField()
    category = models.ForeignKey(ProductCategory, on_delete=models.CASCADE)
    image1 = models.ImageField(upload_to='product_images')
    image2 = models.ImageField(upload_to='product_images')
    image3 = models.ImageField(upload_to='product_images')
    image4 = models.ImageField(upload_to='product_images')


# class ProductVariant(models.Model):
#     product = models.ForeignKey(Product, on_delete=models.CASCADE)
#     variant = models.ForeignKey(Variant, on_delete=models.CASCADE)


# class ProductVariantOption(models.Model):
#     product = models.ForeignKey(Product, on_delete=models.CASCADE)
#     variant_option = models.ForeignKey(VariantOption, on_delete=models.CASCADE)


class Payment(models.Model):
    first_name = models.CharField(max_length=250)
    last_name = models.CharField(max_length=250)
    order_number = models.CharField(max_length=250)
    phone_number = models.CharField(max_length=250)


class Address(models.Model):
    buyer = models.ForeignKey(Buyer, on_delete=models.CASCADE)
    street_address = models.CharField(max_length=100)
    apartment_address = models.CharField(max_length=100)
    country = models.CharField(max_length=250)
    zip = models.CharField(max_length=100)

    def __str__(self):
        return self.buyer.username

    class Meta:
        verbose_name_plural = 'Addresses'


class Order(models.Model):
    buyer = models.ForeignKey(Buyer, on_delete=models.CASCADE)
    ordered = models.BooleanField(default=False)
    ordered_date = models.DateTimeField(null=True, blank=True)
    delivered = models.BooleanField(default=False)
    billing_address = models.ForeignKey(Address, related_name='billing_address', on_delete=models.SET_NULL, blank=True, null=True)
    payment = models.ForeignKey(Payment, on_delete=models.SET_NULL, blank=True, null=True)
    unique_ref = models.CharField(max_length=250)

    @property
    def product_count(self):
        order_items = OrderItem.objects.filter(order_id=self.id)
        products = []
        for order_item in order_items:
            products.append(order_item.product)
        return len(products)


class OrderItem(models.Model):
    order = models.ForeignKey(Order, on_delete=models.CASCADE)
    product = models.ForeignKey(Product, on_delete=models.CASCADE)
    quantity = models.IntegerField(default=1)


class ProductReview(models.Model):
    product = models.ForeignKey(Product, on_delete=models.CASCADE)
    buyer = models.ForeignKey(Buyer, on_delete=models.CASCADE)
    verified_purchase = models.BooleanField(default=False)
    comment = models.CharField(max_length=500, null=True, blank=True)
    image = models.ImageField(upload_to='product_review_images', null=True, blank=True)
    rating = models.DecimalField(decimal_places=1, max_digits=2)

    @property
    def buyer_name(self):
        return f'{self.buyer.first_name} {self.buyer.last_name}'