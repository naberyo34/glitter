import createClient from "openapi-fetch";
import type { paths } from "./schema";

export const glitterApiClient = createClient<paths>({ baseUrl: process.env.GLITTER_API_URL });
