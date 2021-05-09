import { Injectable } from '@angular/core';

export interface Pagination {
  page: number,
  totalPages: number,
  size: number
}
@Injectable({
  providedIn: 'root'
})
export class PaginatorService {

  public pag: Pagination;
  public sizes = [10, 20, 50, 100];
  public load: Function;
  constructor() { }

  public init(loadFn: Function) {
    this.load = loadFn;
    this.reset();
  }

  public reset() {
    this.pag = {
      page: 1,
      size: 10,
      totalPages: 1
    };
  }

  public isDisabledLeft() {
    return this.pag.page === 1;
  }

  public isDisabledRight() {
    return this.pag.page === this.pag.totalPages;
  }

  public goFirst() {
    this.pag.page = 1;
    this.onPageChange();
  }

  public goLeft() {
    this.pag.page = this.pag.page - 1;
    this.onPageChange();
  }

  public goRight() {
    this.pag.page = this.pag.page + 1;
    this.onPageChange();
  }

  public goLast() {
    this.pag.page = this.pag.totalPages;
    this.onPageChange();
  }

  public sizeChange() {
    this.pag.page = 1;
    this.onPageChange();
  }

  public onPageChange() {
    if (this.pag.page < 1 || this.pag.page > this.pag.totalPages) {
      this.pag.page = 1;
    }
    this.load();
  }
}
