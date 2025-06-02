import { UserManager, WebStorageStateStore } from "oidc-client-ts";

const oidcConfig = {
  authority: "http://localhost:8090", // URL твоего Spring Authorization Server
  client_id: "my-client", // clientId из RegisteredClient
  redirect_uri: "http://localhost:5173/callback", // должен совпадать с redirectUri в RegisteredClient
  response_type: "code",
  scope: "openid profile read write",
  post_logout_redirect_uri: "http://localhost:5173/",
  userStore: new WebStorageStateStore({ store: window.localStorage }),
};

export const userManager = new UserManager(oidcConfig);

export function login() {
  userManager.signinRedirect();
}

export function logout() {
  userManager.signoutRedirect();
}

export async function handleCallback() {
  return userManager.signinRedirectCallback();
}

export async function getUser() {
  return userManager.getUser();
}
