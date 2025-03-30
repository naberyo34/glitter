import { unstable_createContext } from "react-router";

export type User = {
  id: string;
  username: string;
  email: string;
  profile?: string;
}

export const userContext = unstable_createContext<User | null>(null);
