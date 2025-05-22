import { login } from "../service/authService";

export const LoginButton = () => (
  <button
    type="button"
    onClick={login}
    className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none"
  >
    Войти
  </button>
);
