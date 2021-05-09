import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteNotificacioDialogComponent } from './delete-notificacio-dialog.component';

describe('DeleteNotificacioDialogComponent', () => {
  let component: DeleteNotificacioDialogComponent;
  let fixture: ComponentFixture<DeleteNotificacioDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeleteNotificacioDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteNotificacioDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
