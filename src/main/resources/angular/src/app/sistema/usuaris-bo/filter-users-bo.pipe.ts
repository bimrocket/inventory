import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'filterUsersBo',
    pure: false
})
export class FilterUsersBoPipe implements PipeTransform {
    transform(items: any[], filter: Object): any {
        if (!items || !filter) {
            return items;
        }
        // filter items array, items which match and return true will be
        // kept, false will be filtered out
        return items.filter(item => {
            if (item.fullName.includes(filter)) {
                return true;
            } else if (item.email.includes(filter)) {
                return true;
            } else if (filter === 'LDAP') {
                return item.isLdapUser;
            } else if (item.city.includes(filter)) {
                return true;
            } else if (item.profile.description.includes(filter)) {
                return true;
            } else {
                return false;
            }
        });
    }
}