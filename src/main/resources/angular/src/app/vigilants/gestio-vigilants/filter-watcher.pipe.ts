import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'filterWatcher',
    pure: false
})
export class FilterWatcherPipe implements PipeTransform {
    transform(items: any[], filter: any): any {
        if (!items || !filter) {
            return items;
        }
        // filter items array, items which match and return true will be
        // kept, false will be filtered out
        return items.filter(item => {
            if (item.fullName.toUpperCase().includes(filter.toUpperCase())) {
                return true;
            } else if (item.email.toUpperCase().includes(filter.toUpperCase())) {
                return true;
            } else {
                return false;
            }
        });
    }
}