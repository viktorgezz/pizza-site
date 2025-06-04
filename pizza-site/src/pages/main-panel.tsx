import { useEffect, useState } from "react";
import DefaultLayout from "@/layouts/default";
import { Card, CardBody, CardHeader } from "@heroui/card";
import { Button } from "@heroui/button";
import { Select, SelectItem } from "@heroui/select";
import { Input } from "@heroui/input";
import { Modal, ModalContent, ModalHeader, ModalBody, ModalFooter } from "@heroui/modal";
import { Table, TableHeader, TableColumn, TableBody, TableRow, TableCell } from "@heroui/table";
import { useDisclosure } from "@heroui/use-disclosure";
import { getUser } from "../service/authService";
import { useNavigate } from "react-router-dom";

interface Employee {
  id_employee: number;
  username: string;
  role: string;
  jobDescription: string;
  email: string;
  addressRestaurant: string;
}

interface Restaurant {
  id: number;
  address: string;
  status: 'OPEN' | 'CLOSED';
  openingTime: string;
  closingTime: string;
}

export default function MainPanelPage() {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [filteredEmployees, setFilteredEmployees] = useState<Employee[]>([]);
  const [selectedRole, setSelectedRole] = useState<string>("ALL");
  const [restaurant, setRestaurant] = useState<Restaurant | null>(null);
  
  const { isOpen: isEmployeeModalOpen, onOpen: onEmployeeModalOpen, onClose: onEmployeeModalClose } = useDisclosure();
  const { isOpen: isRestaurantModalOpen, onOpen: onRestaurantModalOpen, onClose: onRestaurantModalClose } = useDisclosure();

  const [newEmployee, setNewEmployee] = useState({
    username: "",
    password: "",
    jobDescription: "",
    email: "",
    role: "WORKER"
  });

  const [updatedRestaurant, setUpdatedRestaurant] = useState({
    address: "",
    status: "OPEN",
    openingTime: "",
    closingTime: ""
  });

  const navigate = useNavigate();

  useEffect(() => {
    fetchEmployees();
    fetchRestaurant();
  }, []);

  const fetchEmployees = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        throw new Error("Not authenticated");
      }
      const response = await fetch('http://localhost:8080/employee/all', {
        headers: {
          Authorization: `Bearer ${user.access_token}`,
        },
      });
      const data = await response.json();
      setEmployees(data);
      setFilteredEmployees(data);
    } catch (error) {
      console.error('Error fetching employees:', error);
    }
  };

  const fetchRestaurant = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        throw new Error("Not authenticated");
      }
      const response = await fetch('http://localhost:8080/restaurant', {
        headers: {
          Authorization: `Bearer ${user.access_token}`,
        },
      });
      const data = await response.json();
      setRestaurant(data);
      setUpdatedRestaurant({
        address: data.address,
        status: data.status,
        openingTime: data.openingTime,
        closingTime: data.closingTime
      });
    } catch (error) {
      console.error('Error fetching restaurant:', error);
    }
  };

  const handleRoleFilter = async (role: string) => {
    setSelectedRole(role);
    if (role === "ALL") {
      setFilteredEmployees(employees);
    } else {
      try {
        const user = await getUser();
        if (!user || !user.access_token) {
          throw new Error("Not authenticated");
        }
        const response = await fetch(`http://localhost:8080/employee/all/${role}`, {
          headers: {
            Authorization: `Bearer ${user.access_token}`,
          },
        });
        const data = await response.json();
        setFilteredEmployees(data);
      } catch (error) {
        console.error('Error filtering employees:', error);
      }
    }
  };

  const handleHireEmployee = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        throw new Error("Not authenticated");
      }
      const response = await fetch(`http://localhost:8080/employee/${newEmployee.role}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.access_token}`,
        },
        body: JSON.stringify(newEmployee),
      });
      if (response.ok) {
        onEmployeeModalClose();
        fetchEmployees();
        setNewEmployee({
          username: "",
          password: "",
          jobDescription: "",
          email: "",
          role: "WORKER"
        });
      }
    } catch (error) {
      console.error('Error hiring employee:', error);
    }
  };

  const handleFireEmployee = async (username: string) => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        throw new Error("Not authenticated");
      }
      await fetch(`http://localhost:8080/employee/${username}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${user.access_token}`,
        },
      });
      fetchEmployees();
    } catch (error) {
      console.error('Error firing employee:', error);
    }
  };

  const handleUpdateRestaurant = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        throw new Error("Not authenticated");
      }
      await fetch(`http://localhost:8080/restaurant/${restaurant?.address}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.access_token}`,
        },
        body: JSON.stringify(updatedRestaurant),
      });
      onRestaurantModalClose();
      fetchRestaurant();
    } catch (error) {
      console.error('Error updating restaurant:', error);
    }
  };

  const handleUpdateStatus = async (status: 'OPEN' | 'CLOSED') => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        throw new Error("Not authenticated");
      }
      await fetch(`http://localhost:8080/restaurant/${restaurant?.address}/status?status=${status}`, {
        method: 'PATCH',
        headers: {
          Authorization: `Bearer ${user.access_token}`,
        },
      });
      fetchRestaurant();
    } catch (error) {
      console.error('Error updating status:', error);
    }
  };

  return (
    <DefaultLayout>
      <div className="container mx-auto px-4 py-8 grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Restaurant Management */}
        <Card>
          <CardHeader className="flex justify-between items-center">
            <h2 className="text-2xl font-bold">Управление рестораном</h2>
            <Button color="primary" onClick={onRestaurantModalOpen}>
              Изменить данные
            </Button>
          </CardHeader>
          <CardBody>
            {restaurant && (
              <div className="space-y-4">
                <p><strong>Адрес:</strong> {restaurant.address}</p>
                <p><strong>Статус:</strong> {restaurant.status}</p>
                <p><strong>Время работы:</strong> {restaurant.openingTime} - {restaurant.closingTime}</p>
                <div className="flex gap-2">
                  <Button
                    color={restaurant.status === 'OPEN' ? 'success' : 'default'}
                    onClick={() => handleUpdateStatus('OPEN')}
                  >
                    Открыть
                  </Button>
                  <Button
                    color={restaurant.status === 'CLOSED' ? 'danger' : 'default'}
                    onClick={() => handleUpdateStatus('CLOSED')}
                  >
                    Закрыть
                  </Button>
                </div>
              </div>
            )}
          </CardBody>
        </Card>

        {/* Employee Management */}
        <Card>
          <CardHeader className="flex justify-between items-center">
            <h2 className="text-2xl font-bold">Управление сотрудниками</h2>
            <div className="flex gap-2">
              <select
                className="px-3 py-2 bg-default-100 rounded-lg border-2 border-default-200 focus:border-primary focus:outline-none"
                value={selectedRole}
                onChange={(e) => handleRoleFilter(e.target.value)}
              >
                <option value="ALL">Все</option>
                <option value="MANAGER">Менеджеры</option>
                <option value="WORKER">Работники</option>
                <option value="COURIER">Курьеры</option>
              </select>
              <Button color="primary" onClick={onEmployeeModalOpen}>
                Нанять сотрудника
              </Button>
            </div>
          </CardHeader>
          <CardBody>
            <Table>
              <TableHeader>
                <TableColumn>Имя пользователя</TableColumn>
                <TableColumn>Роль</TableColumn>
                <TableColumn>Email</TableColumn>
                <TableColumn>Действия</TableColumn>
              </TableHeader>
              <TableBody>
                {filteredEmployees.map((employee) => (
                  <TableRow key={employee.id_employee}>
                    <TableCell>{employee.username}</TableCell>
                    <TableCell>{employee.role}</TableCell>
                    <TableCell>{employee.email}</TableCell>
                    <TableCell>
                      <div className="flex gap-2">
                        <Button
                          color="primary"
                          size="sm"
                          onClick={() => navigate(`/employee/${employee.username}`)}
                        >
                          Подробнее
                        </Button>
                        <Button
                          color="danger"
                          size="sm"
                          onClick={() => handleFireEmployee(employee.username)}
                        >
                          Уволить
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardBody>
        </Card>
      </div>

      {/* Hire Employee Modal */}
      <Modal isOpen={isEmployeeModalOpen} onClose={onEmployeeModalClose}>
        <ModalContent>
          <ModalHeader>Нанять сотрудника</ModalHeader>
          <ModalBody>
            <div className="space-y-4">
              <Input
                label="Имя пользователя"
                value={newEmployee.username}
                onChange={(e) => setNewEmployee({...newEmployee, username: e.target.value})}
              />
              <Input
                label="Пароль"
                type="password"
                value={newEmployee.password}
                onChange={(e) => setNewEmployee({...newEmployee, password: e.target.value})}
              />
              <Input
                label="Email"
                type="email"
                value={newEmployee.email}
                onChange={(e) => setNewEmployee({...newEmployee, email: e.target.value})}
              />
              <Input
                label="Описание должности"
                value={newEmployee.jobDescription}
                onChange={(e) => setNewEmployee({...newEmployee, jobDescription: e.target.value})}
              />
              <div className="flex flex-col gap-2">
                <label className="text-sm font-medium">Роль</label>
                <select
                  className="w-full px-3 py-2 bg-default-100 rounded-lg border-2 border-default-200 focus:border-primary focus:outline-none"
                  value={newEmployee.role}
                  onChange={(e) => setNewEmployee({...newEmployee, role: e.target.value})}
                >
                  <option value="MANAGER">Менеджер</option>
                  <option value="WORKER">Работник</option>
                  <option value="COURIER">Курьер</option>
                </select>
              </div>
            </div>
          </ModalBody>
          <ModalFooter>
            <Button color="default" onClick={onEmployeeModalClose}>
              Отмена
            </Button>
            <Button color="primary" onClick={handleHireEmployee}>
              Нанять
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {/* Update Restaurant Modal */}
      <Modal isOpen={isRestaurantModalOpen} onClose={onRestaurantModalClose}>
        <ModalContent>
          <ModalHeader>Изменить данные ресторана</ModalHeader>
          <ModalBody>
            <div className="space-y-4">
              <Input
                label="Адрес"
                value={updatedRestaurant.address}
                onChange={(e) => setUpdatedRestaurant({...updatedRestaurant, address: e.target.value})}
              />
              <Input
                label="Время открытия"
                type="time"
                step="1"
                value={updatedRestaurant.openingTime}
                onChange={(e) => setUpdatedRestaurant({...updatedRestaurant, openingTime: e.target.value})}
              />
              <Input
                label="Время закрытия"
                type="time"
                step="1"
                value={updatedRestaurant.closingTime}
                onChange={(e) => setUpdatedRestaurant({...updatedRestaurant, closingTime: e.target.value})}
              />
            </div>
          </ModalBody>
          <ModalFooter>
            <Button color="default" onClick={onRestaurantModalClose}>
              Отмена
            </Button>
            <Button color="primary" onClick={handleUpdateRestaurant}>
              Сохранить
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </DefaultLayout>
  );
} 