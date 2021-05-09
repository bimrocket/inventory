import { Component, OnInit } from '@angular/core';
import { PaginatorService } from '../../services/paginator.service';

@Component({
  selector: 'app-paginator',
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.scss']
})
export class PaginatorComponent implements OnInit {

  constructor(public paginator: PaginatorService) { 
    
  }

  ngOnInit() {
  }

}
