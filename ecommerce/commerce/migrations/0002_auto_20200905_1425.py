# Generated by Django 3.1 on 2020-09-05 11:25

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('commerce', '0001_initial'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='address',
            name='default',
        ),
        migrations.RemoveField(
            model_name='productreview',
            name='order',
        ),
        migrations.AddField(
            model_name='productreview',
            name='product',
            field=models.ForeignKey(default=1, on_delete=django.db.models.deletion.CASCADE, to='commerce.product'),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='productreview',
            name='rating',
            field=models.DecimalField(decimal_places=1, default=1, max_digits=2),
            preserve_default=False,
        ),
        migrations.AlterField(
            model_name='productreview',
            name='comment',
            field=models.CharField(blank=True, max_length=500, null=True),
        ),
        migrations.AlterField(
            model_name='productreview',
            name='image',
            field=models.ImageField(blank=True, null=True, upload_to='product_review_images'),
        ),
    ]
