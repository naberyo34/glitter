import createClient from "openapi-fetch";
import { url } from "~/lib/url.server";
import type { paths } from "./schema";

export const glitterApiClient = createClient<paths>({ baseUrl: url.api });
