import { Injectable } from '@angular/core';
import { saveAs } from 'file-saver';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  constructor() { }


  seeFile(fileDocument,fileType,fileName){
    var bytes = this.base64ToArrayBuffer(fileDocument);

    var blob = new Blob([bytes], { type: fileType });
    var file = new File([blob], fileName, {type: fileType, lastModified: Date.now()});
    saveAs(file, fileName,fileType);   
  }

  base64ToArrayBuffer(base64) {
    var binaryString = window.atob(base64);
    var binaryLen = binaryString.length;
    var bytes = new Uint8Array(binaryLen);
    for (var i = 0; i < binaryLen; i++) {
       var ascii = binaryString.charCodeAt(i);
       bytes[i] = ascii;
    }
    return bytes;
 }

}
