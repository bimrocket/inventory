import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'targetes'
})
export class TargetesPipe implements PipeTransform {

  transform(value: any, args?: any): any {
    return null;
  }

}
