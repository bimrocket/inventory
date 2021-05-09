import { Injectable } from '@angular/core';
import { config } from 'config/config';

@Injectable()
export class AuthorizationService {
  constructor() {}

  public logout() {
    localStorage.setItem(config.LOCAL_STORAGE_KEYS.ACCESS_TOKEN, 'false');
    localStorage.setItem(config.LOCAL_STORAGE_KEYS.REFRESH_TOKEN, 'false');
  }

  public setAccessToken(accessToken: string): void {
    localStorage.setItem(config.LOCAL_STORAGE_KEYS.ACCESS_TOKEN, accessToken);
  }

  public setRefreshToken(refreshToken: string): void {
    localStorage.setItem(config.LOCAL_STORAGE_KEYS.REFRESH_TOKEN, refreshToken);
  }

  public getAccessToken(): string {
    return localStorage.getItem(config.LOCAL_STORAGE_KEYS.ACCESS_TOKEN);
  }

  public getRefreshToken(): string {
    return localStorage.getItem(config.LOCAL_STORAGE_KEYS.REFRESH_TOKEN);
  }

  public getBearer(): string {
    return 'Bearer ' + localStorage.getItem(config.LOCAL_STORAGE_KEYS.ACCESS_TOKEN);
  }

}
