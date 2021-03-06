# Generated by Django 3.1 on 2020-09-02 07:22

import django.contrib.auth.models
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('auth', '0012_alter_user_first_name_max_length'),
    ]

    operations = [
        migrations.CreateModel(
            name='Address',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('street_address', models.CharField(max_length=100)),
                ('apartment_address', models.CharField(max_length=100)),
                ('country', models.CharField(max_length=250)),
                ('zip', models.CharField(max_length=100)),
                ('default', models.BooleanField(default=False)),
            ],
            options={
                'verbose_name_plural': 'Addresses',
            },
        ),
        migrations.CreateModel(
            name='Buyer',
            fields=[
                ('user_ptr', models.OneToOneField(auto_created=True, on_delete=django.db.models.deletion.CASCADE, parent_link=True, primary_key=True, serialize=False, to='auth.user')),
                ('phone_number', models.CharField(max_length=12)),
            ],
            options={
                'verbose_name': 'user',
                'verbose_name_plural': 'users',
                'abstract': False,
            },
            bases=('auth.user',),
            managers=[
                ('objects', django.contrib.auth.models.UserManager()),
            ],
        ),
        migrations.CreateModel(
            name='Order',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('ordered', models.BooleanField(default=False)),
                ('ordered_date', models.DateTimeField()),
                ('delivered', models.BooleanField(default=False)),
                ('unique_ref', models.CharField(max_length=250)),
                ('billing_address', models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.SET_NULL, related_name='billing_address', to='commerce.address')),
                ('buyer', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='commerce.buyer')),
            ],
        ),
        migrations.CreateModel(
            name='Payment',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('first_name', models.CharField(max_length=250)),
                ('last_name', models.CharField(max_length=250)),
                ('order_number', models.CharField(max_length=250)),
                ('phone_number', models.CharField(max_length=250)),
            ],
        ),
        migrations.CreateModel(
            name='ProductCategory',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=250)),
                ('description', models.CharField(max_length=250)),
            ],
        ),
        migrations.CreateModel(
            name='ProductReview',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('verified_purchase', models.BooleanField(default=False)),
                ('comment', models.CharField(max_length=500)),
                ('image', models.ImageField(upload_to='product_review_images')),
                ('buyer', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='commerce.buyer')),
                ('order', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='commerce.order')),
            ],
        ),
        migrations.CreateModel(
            name='Product',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=250)),
                ('short_description', models.TextField()),
                ('description', models.TextField()),
                ('unit_price', models.DecimalField(decimal_places=2, max_digits=12)),
                ('stock', models.IntegerField()),
                ('low_stock', models.IntegerField()),
                ('image1', models.ImageField(upload_to='product_images')),
                ('image2', models.ImageField(upload_to='product_images')),
                ('image3', models.ImageField(upload_to='product_images')),
                ('image4', models.ImageField(upload_to='product_images')),
                ('category', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='commerce.productcategory')),
            ],
        ),
        migrations.CreateModel(
            name='OrderItem',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('quantity', models.IntegerField(default=1)),
                ('order', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='commerce.order')),
                ('product', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='commerce.product')),
            ],
        ),
        migrations.AddField(
            model_name='order',
            name='payment',
            field=models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.SET_NULL, to='commerce.payment'),
        ),
        migrations.AddField(
            model_name='address',
            name='buyer',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='commerce.buyer'),
        ),
    ]
