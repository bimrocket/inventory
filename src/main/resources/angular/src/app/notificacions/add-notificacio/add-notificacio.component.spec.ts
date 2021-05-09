import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddNotificacioComponent } from './add-notificacio.component';

describe('AddNotificacioComponent', () => {
  let component: AddNotificacioComponent;
  let fixture: ComponentFixture<AddNotificacioComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddNotificacioComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddNotificacioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
