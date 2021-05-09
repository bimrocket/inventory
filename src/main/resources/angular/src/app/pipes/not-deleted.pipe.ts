import { Pipe, PipeTransform } from '@angular/core';
import { GuildCard } from 'app/api/model/guildCard';

@Pipe({
  name: 'notDeleted'
})
export class NotDeletedPipe implements PipeTransform {

    transform(guild : GuildCard[]): any {
     return guild.filter(objecte => objecte.deletedDate == null);
	 	
	 }

}
