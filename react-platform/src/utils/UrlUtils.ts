const API_URL: string = process.env.REACT_APP_API_URL as string;
const DOMAIN: string = process.env.REACT_APP_DOMAIN as string;

function getAuthorizedUrl(module: string, version: string): string {
  return `${API_URL}/authorized/${module}/${version}`;
}

function getHomeUrl(): string {
  return `${DOMAIN}/home`;
}

export { API_URL, getHomeUrl, getAuthorizedUrl };
