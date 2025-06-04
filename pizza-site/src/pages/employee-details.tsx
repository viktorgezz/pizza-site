import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import DefaultLayout from "@/layouts/default";
import { Card, CardBody, CardHeader } from "@heroui/card";
import { Button } from "@heroui/button";
import { Select, SelectItem } from "@heroui/select";
import { Input } from "@heroui/input";
import { getUser } from "../service/authService";

interface Employee {
  id_employee: number;
  username: string;
  role: string;
  jobDescription: string;
  email: string;
  restaurantId: number;
}

export default function EmployeeDetailsPage() {
  const { username } = useParams();
  const navigate = useNavigate();
  const [employee, setEmployee] = useState<Employee | null>(null);
  const [updatedEmployee, setUpdatedEmployee] = useState({
    username: "",
    password: "",
    jobDescription: "",
    email: "",
    restaurantId: 1,
    role: ""
  });

  useEffect(() => {
    fetchEmployeeDetails();
  }, [username]);

  const fetchEmployeeDetails = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        throw new Error("Not authenticated");
      }
      const response = await fetch(`http://localhost:8080/employee/${username}`, {
        headers: {
          Authorization: `Bearer ${user.access_token}`,
        },
      });
      const data = await response.json();
      setEmployee(data);
      setUpdatedEmployee({
        username: data.username,
        password: "",
        jobDescription: data.jobDescription,
        email: data.email,
        restaurantId: data.restaurantId,
        role: data.role
      });
    } catch (error) {
      console.error('Error fetching employee details:', error);
    }
  };

  const handleUpdateEmployee = async () => {
    try {
      const user = await getUser();
      if (!user || !user.access_token) {
        throw new Error("Not authenticated");
      }
      await fetch(`http://localhost:8080/employee`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${user.access_token}`,
        },
        body: JSON.stringify(updatedEmployee),
      });
      navigate('/main-panel');
    } catch (error) {
      console.error('Error updating employee:', error);
    }
  };

  if (!employee) {
    return <div>Loading...</div>;
  }

  return (
    <DefaultLayout>
      <div className="container mx-auto px-4 py-8">
        <Card>
          <CardHeader className="flex justify-between items-center">
            <h2 className="text-2xl font-bold">Информация о сотруднике</h2>
          </CardHeader>
          <CardBody>
            <div className="space-y-4">
              <Input
                label="Имя пользователя"
                value={updatedEmployee.username}
                onChange={(e) => setUpdatedEmployee({...updatedEmployee, username: e.target.value})}
              />
              <Input
                label="Новый пароль"
                type="password"
                value={updatedEmployee.password}
                onChange={(e) => setUpdatedEmployee({...updatedEmployee, password: e.target.value})}
              />
              <Input
                label="Email"
                type="email"
                value={updatedEmployee.email}
                onChange={(e) => setUpdatedEmployee({...updatedEmployee, email: e.target.value})}
              />
              <Input
                label="Описание должности"
                value={updatedEmployee.jobDescription}
                onChange={(e) => setUpdatedEmployee({...updatedEmployee, jobDescription: e.target.value})}
              />
              <Select
                label="Роль"
                value={updatedEmployee.role}
                onChange={(e) => setUpdatedEmployee({...updatedEmployee, role: e.target.value})}
              >
                <SelectItem key="MANAGER" data-value="MANAGER">Менеджер</SelectItem>
                <SelectItem key="WORKER" data-value="WORKER">Работник</SelectItem>
                <SelectItem key="COURIER" data-value="COURIER">Курьер</SelectItem>
              </Select>
              <div className="flex justify-end gap-2">
                <Button color="default" onClick={() => navigate('/main-panel')}>
                  Отмена
                </Button>
                <Button color="primary" onClick={handleUpdateEmployee}>
                  Сохранить
                </Button>
              </div>
            </div>
          </CardBody>
        </Card>
      </div>
    </DefaultLayout>
  );
} 