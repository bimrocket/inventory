import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteDialogUsuariBoComponent } from './delete-dialog-usuari-bo.component';

describe('DeleteDialogUsuariBoComponent', () => {
  let component: DeleteDialogUsuariBoComponent;
  let fixture: ComponentFixture<DeleteDialogUsuariBoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeleteDialogUsuariBoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteDialogUsuariBoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
