import { Route, Routes } from "react-router-dom";

import IndexPage from "@/pages/index";
import AboutPage from "@/pages/about";
import ProfilePage from "@/pages/profile";
import { CallbackPage } from "@/pages/CallbackPage";
import LoginPage from "@/pages/loginPage";

function App() {
  return (
    <Routes>
      <Route element={<IndexPage />} path="/" />
      <Route element={<AboutPage />} path="/about" />
      <Route element={<ProfilePage />} path="/profile" />
      <Route element={<CallbackPage />} path="/callback" />
      <Route element={<LoginPage />} path="/login" />
    </Routes>
  );
}

export default App;
