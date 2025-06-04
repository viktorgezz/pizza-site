import React, { useEffect, useState } from 'react';
import { Card, CardBody, CardFooter, Button } from '@heroui/react';
import { Link } from 'react-router-dom';
import DefaultLayout from "@/layouts/default";
import Footer from "@/components/Footer";
import { getUser } from "../service/authService";

interface Ingredient {
  title: string;
  quantity: number;
  measure: string;
}

interface MenuItem {
  title: string;
  price: number;
  description: string;
  ingredients: Ingredient[];
  imageBase64: string;
}

type ToastType = 'success' | 'error';

export default function PizzasPage() {
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
  const [isToastVisible, setIsToastVisible] = useState(false);
  const [toastMessage, setToastMessage] = useState('');
  const [toastType, setToastType] = useState<ToastType>('success');

  useEffect(() => {
    fetch('http://localhost:8080/menu/with-images')
      .then(response => response.json())
      .then(data => setMenuItems(data))
      .catch(error => console.error('Error fetching menu:', error));
  }, []);

  const showToast = (message: string, type: ToastType) => {
    setToastMessage(message);
    setToastType(type);
    setIsToastVisible(true);
    setTimeout(() => setIsToastVisible(false), 3000);
  };

  const addToCart = async (title: string, price: number) => {
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
      showToast(`${title} добавлена в корзину`, 'success');
    } catch (error) {
      console.error('Error adding to cart:', error);
      if ((error as Error).message === "Not authenticated") {
        showToast('Пожалуйста, войдите в систему', 'error');
      } else {
        showToast('Не удалось добавить товар в корзину', 'error');
      }
    }
  };

  return (
    <DefaultLayout>
      {isToastVisible && (
        <div 
          className={`fixed top-4 right-4 z-50 p-4 rounded-lg shadow-lg ${
            toastType === 'success' ? 'bg-green-500' : 'bg-red-500'
          } text-white`}
        >
          {toastMessage}
        </div>
      )}
      <div className="container mx-auto px-4 py-8">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold">Меню</h1>
          <Link to="/order">
            <Button 
              color="primary"
              variant="ghost"
              className="flex items-center gap-2"
            >
              <span>Корзина</span>
            </Button>
          </Link>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {menuItems.map((item, index) => (
            <Card key={index} className="w-full max-w-[300px] mx-auto">
              <CardBody className="p-0">
                <img
                  src={`data:image/jpeg;base64,${item.imageBase64}`}
                  alt={item.title}
                  className="w-full h-40 object-contain"
                />
                <div className="p-3">
                  <h2 className="text-lg font-semibold mb-1">{item.title}</h2>
                  <p className="text-sm text-gray-600 mb-1">{item.description}</p>
                  <p className="text-xs text-gray-500">
                    {item.ingredients.map(ing => ing.title).join(', ')}
                  </p>
                </div>
              </CardBody>
              <CardFooter className="flex justify-between items-center p-3">
                <span className="text-lg font-bold">{item.price.toFixed(0)} ₽</span>
                <Button 
                  color="primary" 
                  size="sm"
                  onClick={() => addToCart(item.title, item.price)}
                >
                  В корзину
                </Button>
              </CardFooter>
            </Card>
          ))}
        </div>
      </div>
      <Footer />
    </DefaultLayout>
  );
} 