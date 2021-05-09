import {FormControl} from '@angular/forms';

//Valida el NIE o (NIF, DNI) dels formualris
//Informacio sobre documents que valida https://asepyme.com/diferencias-entre-cif-nif-dni/
export function documentValidator (otherControlName) {

  let thisControl: FormControl;
  let otherControl: FormControl;
  
  return function documentValidate( control: FormControl ) {

    if (!control.parent) {
      return null;
    }

    // Initializing the validator.
    if (!thisControl) {
      thisControl = control;
      otherControl = control.parent.get(otherControlName) as FormControl;
      if (!otherControl) {
        throw new Error('documentValidator(): other control is not found in parent group');
      }
      otherControl.valueChanges.subscribe(() => {
        thisControl.updateValueAndValidity();
      });
    }

    var dni_letters = "TRWAGMYFPDXBNJZSQVHLCKE";
    var DNI_REGEX = /^(\d{8})([A-Z])$/;
    if(thisControl.value == null){
      return null;
    }
    var dniCif = thisControl.value;
    dniCif = dniCif.toUpperCase().replace(/\s/, '');
    if( otherControl.value == '0'){
      var letter = dni_letters.charAt( parseInt( dniCif, 10 ) % 23 );
      if (letter.toUpperCase() != dniCif.charAt(8).toUpperCase() || thisControl.value.length!=9){
        let nie_prefix = dniCif.charAt( 0 ).toUpperCase();
        switch (nie_prefix) {
          case 'X': nie_prefix = 0; break;
          case 'Y': nie_prefix = 1; break;
          case 'Z': nie_prefix = 2; break;
          default:  return { document: true}; 
        }
        dniCif = nie_prefix + dniCif.substr(1);
        var dni_letter = "TRWAGMYFPDXBNJZSQVHLCKE";
        var letterNie = dni_letter.charAt( parseInt( dniCif, 10 ) % 23 );
        if (letterNie.toUpperCase() !== dniCif.charAt(8).toUpperCase() || thisControl.value.length!==9){
          return {
            document: true,
          };
        }
      }
    }else if( otherControl.value == '1'){
    
      const CIF_REGEX = /^([ABCDEFGHJKLMNPQRSUVW])(\d{7})([0-9A-J])$/;
      const match = thisControl.value.toUpperCase().match( CIF_REGEX );

      if(!CIF_REGEX.test( thisControl.value.toUpperCase() )) return { document: true }; 
  
      const letter = match[1], number = match[2], controlDig = match[3];

      let even_sum: any = 0;
      let odd_sum: any = 0;
      let n: number;
  
      for ( let i = 0; i < number.length; i++) {
        n = parseInt( number[i], 10 );
        // Odd positions (Even index equals to odd position. i=0 equals first position)
        if ( i % 2 === 0 ) {
          // Odd positions are multiplied first.
          n *= 2;
          // If the multiplication is bigger than 10 we need to adjust
          odd_sum += n < 10 ? n : n - 9;
        // Even positions
        // Just sum them
        } else {
          even_sum += n;
        }
      }

      let control_digit = (10 - (even_sum + odd_sum).toString().substr(-1) );
      if (control_digit === 10) {
        control_digit = 0;
      }
      const control_letter = 'JABCDEFGHI'.substr( control_digit, 1 );

      if ( letter.match( /[ABEH]/ ) ) { // Control must be a digit
        if(controlDig != control_digit) return { document: true }; 
      } 
      else if ( letter.match( /[KPQS]/ ) ) { // Control must be a letter
        if(controlDig != control_letter) return { document: true }; 
      }
      else { // Can be either
        if((controlDig != control_digit && controlDig != control_letter)) return { document: true }; 
      }

    }
    return null;
  }
}