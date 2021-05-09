import {FormControl} from '@angular/forms';

export function telephoneNumberValidator() {

  let thisControl: FormControl;

  return function telephoneNumberValidate( control: FormControl ) {
    
    // Initializing the validator.
    if (!thisControl) thisControl = control;

    if(thisControl.value.length < 9){
      return {
        document: true,
      };
    }
    
    return null;
  }

}