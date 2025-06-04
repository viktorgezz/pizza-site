import React, { useEffect, useState } from 'react';
import { Button, Card, CardBody, CardFooter } from '@heroui/react';
import DefaultLayout from "@/layouts/default";
import Footer from "@/components/Footer";
import { getUser } from "../service/authService";

interface CartItem {
  title: string;
  price: number;
  quantity: number;
}

type DeliveryType = 'DELIVERY' | 'PICKUP';

export default function OrderPage() {
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [totalPrice, setTotalPrice] = useState<number>(0);
  const [deliveryType, setDeliveryType] = useState<DeliveryType>('DELIVERY');

  const fetchCart = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        throw new Error("Not authenticated");
      }
      const response = await fetch('http://localhost:8080/cart', {
        credentials: 'include',
        headers: {
          Authorization: `Bearer ${user.access_token}`,
        }
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();
      setCartItems(data);
    } catch (error) {
      console.error('Error fetching cart:', error);
    }
  };

  const fetchTotalPrice = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        throw new Error("Not authenticated");
      }
      const response = await fetch('http://localhost:8080/cart/total', {
        credentials: 'include',
        headers: {
          Authorization: `Bearer ${user.access_token}`,
        }
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();
      setTotalPrice(data.total);
    } catch (error) {
      console.error('Error fetching total price:', error);
    }
  };

  const decreaseQuantity = async (title: string) => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        throw new Error("Not authenticated");
      }
      const response = await fetch('http://localhost:8080/cart/decrease', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.access_token}`,
        },
        body: JSON.stringify({ title }),
        credentials: 'include'
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      await fetchCart();
      await fetchTotalPrice();
    } catch (error) {
      console.error('Error decreasing quantity:', error);
    }
  };

  const removeFromCart = async (title: string) => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        throw new Error("Not authenticated");
      }
      const response = await fetch('http://localhost:8080/cart', {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.access_token}`,
        },
        body: JSON.stringify({ title }),
        credentials: 'include'
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      await fetchCart();
      await fetchTotalPrice();
    } catch (error) {
      console.error('Error removing from cart:', error);
    }
  };

  const increaseQuantity = async (title: string, price: number) => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        throw new Error("Not authenticated");
      }
      const response = await fetch('http://localhost:8080/cart/add', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.access_token}`,
        },
        body: JSON.stringify({ title, price }),
        credentials: 'include'
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      await fetchCart();
      await fetchTotalPrice();
    } catch (error) {
      console.error('Error increasing quantity:', error);
    }
  };

  const handleCheckout = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        throw new Error("Not authenticated");
      }
      const response = await fetch('http://localhost:8080/order/checkout', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.access_token}`,
        },
        body: JSON.stringify({
          orderType: deliveryType,
          items: cartItems
        }),
        credentials: 'include'
      });
      
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      
      // Clear cart after successful order
      await fetchCart();
      await fetchTotalPrice();
      alert('Заказ успешно оформлен!');
    } catch (error) {
      console.error('Error checking out:', error);
      alert('Ошибка при оформлении заказа');
    }
  };

  useEffect(() => {
    fetchCart();
    fetchTotalPrice();
  }, []);

  return (
    <DefaultLayout>
      <div className="container mx-auto px-4 py-8 max-w-2xl">
        <h1 className="text-2xl font-bold mb-6">Корзина</h1>
        {cartItems.length === 0 ? (
          <p className="text-center text-gray-500">Корзина пуста</p>
        ) : (
          <>
            <div className="grid gap-3">
              {cartItems.map((item, index) => (
                <Card key={index} className="w-full">
                  <CardBody className="flex justify-between items-center p-3">
                    <div className="flex items-center gap-4 flex-1">
                      <div>
                        <h3 className="text-md font-medium">{item.title}</h3>
                        <div className="flex items-center gap-3 mt-1">
                          <span className="text-sm text-gray-600">Количество: {item.quantity}</span>
                          <span className="font-semibold">{item.price.toFixed(0)} ₽</span>
                        </div>
                      </div>
                    </div>
                    <div className="flex gap-2 ml-4">
                      <Button 
                        color="primary" 
                        variant="light"
                        size="sm"
                        onClick={() => decreaseQuantity(item.title)}
                      >
                        -
                      </Button>
                      <Button 
                        color="primary" 
                        variant="light"
                        size="sm"
                        onClick={() => increaseQuantity(item.title, item.price)}
                      >
                        +
                      </Button>
                      <Button 
                        color="danger" 
                        variant="light"
                        size="sm"
                        onClick={() => removeFromCart(item.title)}
                      >
                        Удалить
                      </Button>
                    </div>
                  </CardBody>
                </Card>
              ))}
            </div>
            <div className="mt-6 bg-white rounded-lg p-4 shadow">
              <h3 className="text-lg font-medium mb-3">Способ получения</h3>
              <div className="space-y-2">
                <label className="flex items-center space-x-2 cursor-pointer">
                  <input
                    type="radio"
                    name="deliveryType"
                    value="DELIVERY"
                    checked={deliveryType === 'DELIVERY'}
                    onChange={(e) => setDeliveryType(e.target.value as DeliveryType)}
                    className="form-radio text-blue-600"
                  />
                  <span>Доставка</span>
                </label>
                <label className="flex items-center space-x-2 cursor-pointer">
                  <input
                    type="radio"
                    name="deliveryType"
                    value="PICKUP"
                    checked={deliveryType === 'PICKUP'}
                    onChange={(e) => setDeliveryType(e.target.value as DeliveryType)}
                    className="form-radio text-blue-600"
                  />
                  <span>Самовывоз</span>
                </label>
              </div>
            </div>
            <div className="flex flex-col gap-3 items-end mt-6">
              <p className="text-xl font-bold">
                Итого: {totalPrice.toFixed(0)} ₽
              </p>
              <Button 
                color="primary" 
                size="lg"
                className="w-full md:w-auto"
                onClick={handleCheckout}
              >
                Оформить заказ
              </Button>
            </div>
          </>
        )}
      </div>
      <Footer />
    </DefaultLayout>
  );
} 