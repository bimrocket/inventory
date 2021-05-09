import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'guildCardStatusIDtoText'
})
export class GuildCardStatusIDtoTextPipe implements PipeTransform {

  transform(value: number): any {
   	 switch(value){
		case 1:{
			return "En estoc";
		}
		case 2:{
			return "Activa";
		}
		case 3:{
			return "Operativa";
		}
		case 4:{
			return "Desactivada"; 
		}
		case 5:{
			return "Anul·lada"; 
		}
		case 6:{
			return "Caducada"; 
		}
		case 7:{
			return "Assignada";
		}
		default: {
			return "No definit";
		}
	 }
  }

}
