import { Route, Routes } from "react-router-dom";

import IndexPage from "@/pages/index";
import AboutPage from "@/pages/about";
import ProfilePage from "@/pages/profile";
import { CallbackPage } from "@/pages/CallbackPage";
import LoginPage from "@/pages/loginPage";
import RegisterPage from "@/pages/registerPage";
import MainPanelPage from "@/pages/main-panel";
import EmployeeDetailsPage from "@/pages/employee-details";
import ManagerPanelPage from "@/pages/manager-panel";
import PizzasPage from "@/pages/pizzas";
import WorkerPanelPage from "@/pages/worker-panel";
import CourierPanelPage from "@/pages/courier-panel";
import OrderPage from "@/pages/order";

function App() {
  return (
    <Routes>
      <Route element={<IndexPage />} path="/" />
      <Route element={<AboutPage />} path="/about" />
      <Route element={<ProfilePage />} path="/profile" />
      <Route element={<CallbackPage />} path="/callback" />
      <Route element={<LoginPage />} path="/login" />
      <Route element={<RegisterPage />} path="/register" />
      <Route element={<MainPanelPage />} path="/main-panel" />
      <Route element={<EmployeeDetailsPage />} path="/employee/:username" />
      <Route element={<ManagerPanelPage />} path="/manager-panel" />
      <Route element={<PizzasPage />} path="/pizzas" />
      <Route element={<OrderPage />} path="/order" />
      <Route element={<WorkerPanelPage />} path="/worker-panel" />
      <Route element={<CourierPanelPage />} path="/courier-panel" />
    </Routes>
  );
}

export default App;
