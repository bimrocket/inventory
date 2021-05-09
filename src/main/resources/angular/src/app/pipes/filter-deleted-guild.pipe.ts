import { Pipe, PipeTransform } from '@angular/core';
import { Guild } from 'app/api/model/guild';

@Pipe({
  name: 'filterDeletedGuild'
})
export class FilterDeletedGuildPipe implements PipeTransform {

  transform(guild : Guild[]): any {
     return guild.filter(objecte => objecte.deletedDate == null);
	 	
	 }
  }

