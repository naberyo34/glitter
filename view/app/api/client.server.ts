import createClient from "openapi-fetch";
import { appUrl } from "~/lib/appUrl.server";
import type { paths } from "./schema";

export const glitterApiClient = createClient<paths>({ baseUrl: appUrl.api });
