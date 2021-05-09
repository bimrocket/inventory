import { Injectable } from '@angular/core';
import { saveAs } from 'file-saver';

@Injectable({
  providedIn: 'root'
})
export class PlateNumberService {

  constructor() { }


  change(plateNumber){
    return plateNumber.replace(/\s/g, "") // eliminem espais en blanc 
    .replace(/\-/g, "") // eliminem guions 
    .toUpperCase(); // convertim lletres a majúscules
  }
 

}
