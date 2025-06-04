import { useEffect, useState } from "react";
import DefaultLayout from "@/layouts/default";
import { Card, CardBody, CardHeader } from "@heroui/card";
import { Button } from "@heroui/button";
import { Input } from "@heroui/input";
import { Select, SelectItem } from "@heroui/select";
import { Modal, ModalContent, ModalHeader, ModalBody, ModalFooter } from "@heroui/modal";
import { Table, TableHeader, TableColumn, TableBody, TableRow, TableCell } from "@heroui/table";
import { useDisclosure } from "@heroui/use-disclosure";
import { getUser } from "../service/authService";
import { Measure } from "../types/measure";

interface Ingredient {
  id: number;
  title: string;
  quantity: number;
  measure: Measure;
}

interface MenuItem {
  id: number;
  idRestaurant: number;
  title: string;
  price: number;
  description: string;
  imageUrl: string;
  image?: File;
  ingredients: {
    ingredientTitle: string;
    quantity: number;
    measure: Measure;
  }[];
}

export default function ManagerPanelPage() {
  // States for ingredients
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);
  const [selectedIngredient, setSelectedIngredient] = useState<Ingredient | null>(null);
  const { isOpen: isIngredientModalOpen, onOpen: onIngredientModalOpen, onClose: onIngredientModalClose } = useDisclosure();
  const { isOpen: isUpdateQuantityModalOpen, onOpen: onUpdateQuantityModalOpen, onClose: onUpdateQuantityModalClose } = useDisclosure();
  const { isOpen: isUpdateInfoModalOpen, onOpen: onUpdateInfoModalOpen, onClose: onUpdateInfoModalClose } = useDisclosure();
  
  // States for menu items
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
  const [selectedMenuItem, setSelectedMenuItem] = useState<MenuItem | null>(null);
  const { isOpen: isMenuItemModalOpen, onOpen: onMenuItemModalOpen, onClose: onMenuItemModalClose } = useDisclosure();
  const { isOpen: isUpdateMenuItemModalOpen, onOpen: onUpdateMenuItemModalOpen, onClose: onUpdateMenuItemModalClose } = useDisclosure();

  // Form states
  const [newIngredient, setNewIngredient] = useState({
    title: "",
    measure: "KG" as Measure
  });

  const [updateQuantity, setUpdateQuantity] = useState({
    title: "",
    quantity: 0
  });

  const [updateInfo, setUpdateInfo] = useState({
    oldTitle: "",
    newTitle: "",
    newMeasure: "KG" as Measure
  });

  const [newMenuItem, setNewMenuItem] = useState({
    idRestaurant: 1,
    title: "",
    price: 0,
    description: "",
    image: null as File | null,
    ingredients: [] as {
      ingredientTitle: string;
      quantity: number;
      measure: Measure;
    }[]
  });

  useEffect(() => {
    fetchIngredients();
    fetchMenuItems();
  }, []);

  const fetchIngredients = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) throw new Error("Not authenticated");

      const response = await fetch('http://localhost:8080/ingredient/all', {
        headers: {
          Authorization: `Bearer ${user.access_token}`,
        },
      });
      const data = await response.json();
      setIngredients(data);
    } catch (error) {
      console.error('Error fetching ingredients:', error);
    }
  };

  const fetchMenuItems = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) throw new Error("Not authenticated");

      const response = await fetch('http://localhost:8080/menu/all', {
        headers: {
          Authorization: `Bearer ${user.access_token}`,
        },
      });
      const data = await response.json();
      setMenuItems(data);
    } catch (error) {
      console.error('Error fetching menu items:', error);
    }
  };

  const handleAddIngredient = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) throw new Error("Not authenticated");

      await fetch('http://localhost:8080/ingredient', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.access_token}`,
        },
        body: JSON.stringify(newIngredient),
      });
      
      onIngredientModalClose();
      fetchIngredients();
      setNewIngredient({ title: "", measure: "KG" });
    } catch (error) {
      console.error('Error adding ingredient:', error);
    }
  };

  const handleUpdateQuantity = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) throw new Error("Not authenticated");

      if (updateQuantity.quantity < 0) {
        alert("Количество не может быть отрицательным");
        return;
      }

      await fetch('http://localhost:8080/ingredient', {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.access_token}`,
        },
        body: JSON.stringify(updateQuantity),
      });
      
      onUpdateQuantityModalClose();
      fetchIngredients();
    } catch (error) {
      console.error('Error updating quantity:', error);
    }
  };

  const handleUpdateInfo = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) throw new Error("Not authenticated");

      await fetch('http://localhost:8080/ingredient', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.access_token}`,
        },
        body: JSON.stringify(updateInfo),
      });
      
      onUpdateInfoModalClose();
      fetchIngredients();
    } catch (error) {
      console.error('Error updating info:', error);
    }
  };

  const handleDeleteIngredient = async (title: string) => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) throw new Error("Not authenticated");

      await fetch(`http://localhost:8080/ingredient/${title}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${user.access_token}`,
        },
      });
      
      fetchIngredients();
    } catch (error) {
      console.error('Error deleting ingredient:', error);
    }
  };

  return (
    <DefaultLayout>
      <div className="container mx-auto px-4 py-8 grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Ingredients Management */}
        <Card>
          <CardHeader className="flex justify-between items-center">
            <h2 className="text-2xl font-bold">Управление ингредиентами</h2>
            <Button color="primary" onClick={onIngredientModalOpen}>
              Добавить ингредиент
            </Button>
          </CardHeader>
          <CardBody>
            <div className="h-[600px] overflow-auto">
              <Table>
                <TableHeader>
                  <TableColumn>Название</TableColumn>
                  <TableColumn>Количество</TableColumn>
                  <TableColumn>Единица измерения</TableColumn>
                  <TableColumn>Действия</TableColumn>
                </TableHeader>
                <TableBody>
                  {ingredients.map((ingredient) => (
                    <TableRow key={ingredient.id}>
                      <TableCell>
                        <Button
                          color="secondary"
                          onClick={() => {
                            setSelectedIngredient(ingredient);
                            setUpdateInfo({
                              oldTitle: ingredient.title,
                              newTitle: ingredient.title,
                              newMeasure: ingredient.measure
                            });
                            onUpdateInfoModalOpen();
                          }}
                        >
                          {ingredient.title}
                        </Button>
                      </TableCell>
                      <TableCell>
                        <Button
                          color="secondary"
                          onClick={() => {
                            setSelectedIngredient(ingredient);
                            setUpdateQuantity({
                              title: ingredient.title,
                              quantity: ingredient.quantity
                            });
                            onUpdateQuantityModalOpen();
                          }}
                        >
                          {ingredient.quantity}
                        </Button>
                      </TableCell>
                      <TableCell>{ingredient.measure}</TableCell>
                      <TableCell>
                        <Button
                          color="danger"
                          size="sm"
                          onClick={() => handleDeleteIngredient(ingredient.title)}
                        >
                          Удалить
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          </CardBody>
        </Card>

        {/* Menu Management */}
        <Card>
          <CardHeader className="flex justify-between items-center">
            <h2 className="text-2xl font-bold">Управление меню</h2>
            <Button color="primary" onClick={onMenuItemModalOpen}>
              Добавить в меню
            </Button>
          </CardHeader>
          <CardBody>
            <div className="h-[600px] overflow-auto">
              <Table>
                <TableHeader>
                  <TableColumn>Название</TableColumn>
                  <TableColumn>Цена</TableColumn>
                  <TableColumn>Действия</TableColumn>
                </TableHeader>
                <TableBody>
                  {menuItems.map((item) => (
                    <TableRow key={item.id}>
                      <TableCell>{item.title}</TableCell>
                      <TableCell>{item.price}</TableCell>
                      <TableCell>
                        <div className="flex gap-2">
                          <Button
                            color="secondary"
                            size="sm"
                            onClick={async () => {
                              try {
                                const user = await getUser();
                                if (!user || !user.access_token) throw new Error("Not authenticated");

                                // Получаем детальную информацию о пункте меню
                                const menuResponse = await fetch(`http://localhost:8080/menu/${item.title}`, {
                                  headers: {
                                    Authorization: `Bearer ${user.access_token}`,
                                  },
                                });
                                const menuItemDetails = await menuResponse.json();

                                // Получаем список всех ингредиентов для меню
                                const ingredientsResponse = await fetch(`http://localhost:8080/menu/ingredients/${item.id}`, {
                                  headers: {
                                    Authorization: `Bearer ${user.access_token}`,
                                  },
                                });
                                const menuIngredients = await ingredientsResponse.json();

                                // Объединяем данные
                                const menuItemWithIngredients = {
                                  ...menuItemDetails,
                                  ingredients: menuIngredients.map((ing: any) => ({
                                    ingredientTitle: ing.title,
                                    quantity: ing.quantity,
                                    measure: ing.measure
                                  }))
                                };

                                setSelectedMenuItem(menuItemWithIngredients);
                                onUpdateMenuItemModalOpen();
                              } catch (error) {
                                console.error('Error fetching menu item details:', error);
                              }
                            }}
                          >
                            Изменить
                          </Button>
                          <Button
                            color="danger"
                            size="sm"
                            onClick={async () => {
                              try {
                                const user = await getUser();
                                if (!user || !user.access_token) throw new Error("Not authenticated");
                                
                                await fetch(`http://localhost:8080/menu/${item.id}`, {
                                  method: 'DELETE',
                                  headers: {
                                    Authorization: `Bearer ${user.access_token}`,
                                  },
                                });
                                fetchMenuItems();
                              } catch (error) {
                                console.error('Error deleting menu item:', error);
                              }
                            }}
                          >
                            Удалить
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          </CardBody>
        </Card>
      </div>

      {/* Add Ingredient Modal */}
      <Modal isOpen={isIngredientModalOpen} onClose={onIngredientModalClose}>
        <ModalContent>
          <ModalHeader>Добавить ингредиент</ModalHeader>
          <ModalBody>
            <div className="space-y-4">
              <Input
                label="Название"
                value={newIngredient.title}
                onChange={(e) => setNewIngredient({...newIngredient, title: e.target.value})}
              />
              <div className="space-y-2">
                <label className="block text-sm font-medium text-gray-700">
                  Единица измерения
                </label>
                <select
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                  value={newIngredient.measure}
                  onChange={(e) => setNewIngredient({...newIngredient, measure: e.target.value as Measure})}
                >
                  <option value="KG">Килограммы</option>
                  <option value="LITERS">Литры</option>
                  <option value="UNIT">Штуки</option>
                </select>
              </div>
            </div>
          </ModalBody>
          <ModalFooter>
            <Button color="default" onClick={onIngredientModalClose}>
              Отмена
            </Button>
            <Button color="primary" onClick={handleAddIngredient}>
              Добавить
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {/* Update Quantity Modal */}
      <Modal isOpen={isUpdateQuantityModalOpen} onClose={onUpdateQuantityModalClose}>
        <ModalContent>
          <ModalHeader>Изменить количество</ModalHeader>
          <ModalBody>
            <div className="space-y-4">
              <Input
                type="number"
                label="Количество"
                value={updateQuantity.quantity.toString()}
                onChange={(e) => setUpdateQuantity({...updateQuantity, quantity: Number(e.target.value)})}
                min="0"
                step="0.1"
              />
            </div>
          </ModalBody>
          <ModalFooter>
            <Button color="default" onClick={onUpdateQuantityModalClose}>
              Отмена
            </Button>
            <Button color="primary" onClick={handleUpdateQuantity}>
              Сохранить
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {/* Update Info Modal */}
      <Modal isOpen={isUpdateInfoModalOpen} onClose={onUpdateInfoModalClose}>
        <ModalContent>
          <ModalHeader>Изменить информацию</ModalHeader>
          <ModalBody>
            <div className="space-y-4">
              <Input
                label="Новое название"
                value={updateInfo.newTitle}
                onChange={(e) => setUpdateInfo({...updateInfo, newTitle: e.target.value})}
              />
              <div className="space-y-2">
                <label className="block text-sm font-medium text-gray-700">
                  Единица измерения
                </label>
                <select
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                  value={updateInfo.newMeasure}
                  onChange={(e) => setUpdateInfo({...updateInfo, newMeasure: e.target.value as Measure})}
                >
                  <option value="KG">Килограммы</option>
                  <option value="LITERS">Литры</option>
                  <option value="UNIT">Штуки</option>
                </select>
              </div>
            </div>
          </ModalBody>
          <ModalFooter>
            <Button color="default" onClick={onUpdateInfoModalClose}>
              Отмена
            </Button>
            <Button color="primary" onClick={handleUpdateInfo}>
              Сохранить
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {/* Add Menu Item Modal */}
      <Modal isOpen={isMenuItemModalOpen} onClose={onMenuItemModalClose}>
        <ModalContent>
          <ModalHeader>Добавить в меню</ModalHeader>
          <ModalBody>
            <div className="space-y-4">
              <Input
                label="Название"
                value={newMenuItem.title}
                onChange={(e) => setNewMenuItem({...newMenuItem, title: e.target.value})}
              />
              <Input
                type="number"
                label="Цена"
                value={newMenuItem.price.toString()}
                onChange={(e) => setNewMenuItem({...newMenuItem, price: Number(e.target.value)})}
                min="0"
              />
              <Input
                label="Описание"
                value={newMenuItem.description}
                onChange={(e) => setNewMenuItem({...newMenuItem, description: e.target.value})}
              />
              <Input
                type="file"
                label="Изображение"
                onChange={(e) => {
                  const file = e.target.files?.[0];
                  if (file) {
                    setNewMenuItem({...newMenuItem, image: file});
                  }
                }}
              />
              {/* Ingredients section */}
              <div className="space-y-2">
                <h3 className="text-lg font-semibold">Ингредиенты</h3>
                {newMenuItem.ingredients.map((ing, index) => (
                  <div key={index} className="flex gap-2">
                    <div className="flex-1">
                      <select
                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                        value={ing.ingredientTitle}
                        onChange={(e) => {
                          const newIngredients = [...newMenuItem.ingredients];
                          newIngredients[index].ingredientTitle = e.target.value;
                          newIngredients[index].measure = ingredients.find(i => i.title === e.target.value)?.measure || "KG";
                          setNewMenuItem({...newMenuItem, ingredients: newIngredients});
                        }}
                      >
                        <option value="">Выберите ингредиент</option>
                        {ingredients.map(i => (
                          <option key={i.id} value={i.title}>{i.title}</option>
                        ))}
                      </select>
                    </div>
                    <div className="w-32">
                      <Input
                        type="number"
                        value={ing.quantity.toString()}
                        onChange={(e) => {
                          const newIngredients = [...newMenuItem.ingredients];
                          newIngredients[index].quantity = Number(e.target.value);
                          setNewMenuItem({...newMenuItem, ingredients: newIngredients});
                        }}
                        min="0"
                        step="0.0001"
                      />
                    </div>
                    <Button
                      color="danger"
                      onClick={() => {
                        const newIngredients = newMenuItem.ingredients.filter((_, i) => i !== index);
                        setNewMenuItem({...newMenuItem, ingredients: newIngredients});
                      }}
                    >
                      Удалить
                    </Button>
                  </div>
                ))}
                <Button
                  color="secondary"
                  onClick={() => {
                    setNewMenuItem({
                      ...newMenuItem,
                      ingredients: [
                        ...newMenuItem.ingredients,
                        { ingredientTitle: "", quantity: 0, measure: "KG" }
                      ]
                    });
                  }}
                >
                  Добавить ингредиент
                </Button>
              </div>
            </div>
          </ModalBody>
          <ModalFooter>
            <Button color="default" onClick={onMenuItemModalClose}>
              Отмена
            </Button>
            <Button
              color="primary"
              onClick={async () => {
                try {
                  const user = await getUser();
                  if (!user || !user.access_token) throw new Error("Not authenticated");

                  const formData = new FormData();
                  formData.append("idRestaurant", newMenuItem.idRestaurant.toString());
                  formData.append("title", newMenuItem.title);
                  formData.append("price", newMenuItem.price.toString());
                  formData.append("description", newMenuItem.description);
                  if (newMenuItem.image) {
                    formData.append("image", newMenuItem.image);
                  }
                  formData.append("ingredients", JSON.stringify(newMenuItem.ingredients));

                  await fetch('http://localhost:8080/menu', {
                    method: 'POST',
                    headers: {
                      Authorization: `Bearer ${user.access_token}`,
                    },
                    body: formData,
                  });

                  onMenuItemModalClose();
                  fetchMenuItems();
                  setNewMenuItem({
                    idRestaurant: 1,
                    title: "",
                    price: 0,
                    description: "",
                    image: null,
                    ingredients: []
                  });
                } catch (error) {
                  console.error('Error adding menu item:', error);
                }
              }}
            >
              Добавить
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {/* Update Menu Item Modal */}
      <Modal isOpen={isUpdateMenuItemModalOpen} onClose={onUpdateMenuItemModalClose}>
        <ModalContent>
          <ModalHeader>Изменить пункт меню</ModalHeader>
          <ModalBody>
            {selectedMenuItem && (
              <div className="space-y-4">
                <Input
                  label="Название"
                  value={selectedMenuItem.title}
                  onChange={(e) => setSelectedMenuItem({...selectedMenuItem, title: e.target.value})}
                />
                <Input
                  type="number"
                  label="Цена"
                  value={selectedMenuItem.price.toString()}
                  onChange={(e) => setSelectedMenuItem({...selectedMenuItem, price: Number(e.target.value)})}
                  min="0"
                />
                <Input
                  label="Описание"
                  value={selectedMenuItem.description}
                  onChange={(e) => setSelectedMenuItem({...selectedMenuItem, description: e.target.value})}
                />
                <Input
                  type="file"
                  label="Новое изображение"
                  onChange={(e) => {
                    const file = e.target.files?.[0];
                    if (file) {
                      setSelectedMenuItem({...selectedMenuItem, image: file});
                    }
                  }}
                />
                {/* Ingredients section */}
                <div className="space-y-2">
                  <div className="flex justify-between items-center">
                    <h3 className="text-lg font-semibold">Ингредиенты</h3>
                    <Button
                      color="secondary"
                      size="sm"
                      onClick={() => {
                        const currentIngredients = selectedMenuItem.ingredients || [];
                        setSelectedMenuItem({
                          ...selectedMenuItem,
                          ingredients: [
                            ...currentIngredients,
                            { ingredientTitle: "", quantity: 0, measure: "KG" }
                          ]
                        });
                      }}
                    >
                      Добавить ингредиент
                    </Button>
                  </div>
                  <div className="max-h-60 overflow-y-auto space-y-2">
                    {(selectedMenuItem.ingredients || []).map((ing, index) => (
                      <div key={index} className="flex gap-2 items-center p-2 bg-gray-50 rounded-lg">
                        <div className="flex-1">
                          <select
                            className="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                            value={ing.ingredientTitle}
                            onChange={(e) => {
                              const newIngredients = [...(selectedMenuItem.ingredients || [])];
                              newIngredients[index].ingredientTitle = e.target.value;
                              newIngredients[index].measure = ingredients.find(i => i.title === e.target.value)?.measure || "KG";
                              setSelectedMenuItem({...selectedMenuItem, ingredients: newIngredients});
                            }}
                          >
                            <option value="">Выберите ингредиент</option>
                            {ingredients.map(i => (
                              <option key={i.id} value={i.title}>{i.title}</option>
                            ))}
                          </select>
                        </div>
                        <div className="w-32">
                          <Input
                            type="number"
                            value={ing.quantity.toString()}
                            onChange={(e) => {
                              const newIngredients = [...(selectedMenuItem.ingredients || [])];
                              newIngredients[index].quantity = Number(e.target.value);
                              setSelectedMenuItem({...selectedMenuItem, ingredients: newIngredients});
                            }}
                            min="0"
                            step="0.0001"
                          />
                        </div>
                        <Button
                          color="danger"
                          size="sm"
                          onClick={() => {
                            const newIngredients = (selectedMenuItem.ingredients || []).filter((_, i) => i !== index);
                            setSelectedMenuItem({...selectedMenuItem, ingredients: newIngredients});
                          }}
                        >
                          Удалить
                        </Button>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )}
          </ModalBody>
          <ModalFooter>
            <Button color="default" onClick={onUpdateMenuItemModalClose}>
              Отмена
            </Button>
            <Button
              color="primary"
              onClick={async () => {
                try {
                  const user = await getUser();
                  if (!user || !user.access_token) throw new Error("Not authenticated");

                  const formData = new FormData();
                  formData.append("id", selectedMenuItem!.id.toString());
                  formData.append("title", selectedMenuItem!.title);
                  formData.append("price", selectedMenuItem!.price.toString());
                  formData.append("description", selectedMenuItem!.description);
                  if (selectedMenuItem!.image) {
                    formData.append("image", selectedMenuItem!.image);
                  }
                  formData.append("ingredients", JSON.stringify(selectedMenuItem!.ingredients || []));

                  await fetch('http://localhost:8080/menu', {
                    method: 'PUT',
                    headers: {
                      Authorization: `Bearer ${user.access_token}`,
                    },
                    body: formData,
                  });

                  onUpdateMenuItemModalClose();
                  fetchMenuItems();
                } catch (error) {
                  console.error('Error updating menu item:', error);
                }
              }}
            >
              Сохранить
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </DefaultLayout>
  );
} 