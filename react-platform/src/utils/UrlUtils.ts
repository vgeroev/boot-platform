const DOMAIN: string = process.env.REACT_APP_DOMAIN as string;

function getAuthorizedUrl(module: string, version: string): string {
  return `/authorized/${module}/${version}`;
}

function getAnonPlatformUrl(): string {
  return `/anon/platform/v1`;
}

function getHomeUrl(): string {
  return `${DOMAIN}/home`;
}

export { getHomeUrl, getAuthorizedUrl, getAnonPlatformUrl };
