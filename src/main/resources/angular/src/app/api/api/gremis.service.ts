import { Inject, Injectable, Optional } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpResponse, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { CustomHttpUrlEncodingCodec } from '../encoder';
import { BASE_PATH } from '../variables';
import { Configuration } from '../configuration';

@Injectable({
  providedIn: 'root'
})
export class GremisService {

  protected basePath = 'http://maas.nexusgeografics.com/api';
  public defaultHeaders = new HttpHeaders();
  public configuration = new Configuration();

  constructor(protected httpClient: HttpClient, @Optional()@Inject(BASE_PATH) basePath: string,  @Optional() configuration: Configuration) {
    if (basePath) {
      this.basePath = basePath;
    }
    if (configuration) {
      this.configuration = configuration;
      this.basePath = basePath || configuration.basePath || this.basePath;
    }
  }

  public getGremis( authorization:string,agremiat?: string, dni?: string, permis?: string, observe?: 'body', reportProgress?: boolean): Observable<any>;
  public getGremis( authorization:string,agremiat?: string, dni?: string, permis?: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
  public getGremis( authorization:string,agremiat?: string, dni?: string, permis?: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
  public getGremis( authorization:string,agremiat?: string, dni?: string, permis?: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

    if (authorization === null || authorization === undefined) {
      throw new Error('Required parameter authorization was null or undefined when calling users.');
    }

    let headers = this.defaultHeaders;
    if (authorization !== undefined && authorization !== null) {
      headers = headers.set('Authorization', String(authorization));
    }

    // to determine the Accept header
    let httpHeaderAccepts: string[] = [
      'application/json'
    ];
    let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    if (httpHeaderAcceptSelected != undefined) {
        headers = headers.set("Accept", httpHeaderAcceptSelected);
    }

    let queryParameters = new HttpParams({ encoder: new CustomHttpUrlEncodingCodec() });
    if (agremiat !== undefined && agremiat) {
        queryParameters = queryParameters.set('agremiat', <any>agremiat);
    }
    if (dni !== undefined && dni) {
        queryParameters = queryParameters.set('dni', <any>dni);
    }
    if (permis !== undefined && permis) {
        queryParameters = queryParameters.set('permis', <any>permis);
    }

    return this.httpClient.get<any>(`${this.basePath}/gremis`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
    );

  }

  public deleteGremis( authorization: string, gremiId:number, observe?: 'body', reportProgress?: boolean): Observable<any>;
  public deleteGremis( authorization: string, gremiId:number, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
  public deleteGremis( authorization: string, gremiId:number, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
  public deleteGremis( authorization: string, gremiId:number, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

    if (authorization === null || authorization === undefined) {
      throw new Error('Required parameter authorization was null or undefined when calling users.');
    }

    if (gremiId === null || gremiId === undefined) {
      throw new Error('Required parameter gremiId was null or undefined when calling gremiId.');
    }
    if (gremiId === null || gremiId === undefined) {
        throw new Error('Required parameter authorization was null or undefined when calling gremiId.');
    }

    let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});

    let headers = this.defaultHeaders;
    if (authorization !== undefined && authorization !== null) {
      headers = headers.set('Authorization', String(authorization));
    }

    // to determine the Accept header
    let httpHeaderAccepts: string[] = [
      'application/json'
    ];
    let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    if (httpHeaderAcceptSelected != undefined) {
        headers = headers.set("Accept", httpHeaderAcceptSelected);
    }

    return this.httpClient.delete<any>(`${this.basePath}/gremi/${encodeURIComponent(String(gremiId))}`,
        {
            params: queryParameters,
            withCredentials: this.configuration.withCredentials,
            headers: headers,
            observe: observe,
            reportProgress: reportProgress
        }
    );
  }

}
