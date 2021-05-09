import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'toSixDigits'
})
export class ToSixDigitsPipe implements PipeTransform {

     transform(number : number): any {
     var x = number.toString();
     var y = "0";
     while(x.length < 6){
     	x = y.concat(x);
     }
     return x;
	 	
	 }
}
