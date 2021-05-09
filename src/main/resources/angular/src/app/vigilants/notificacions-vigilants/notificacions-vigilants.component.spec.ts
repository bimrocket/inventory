import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificacionsVigilantsComponent } from './notificacions-vigilants.component';

describe('NotificacionsVigilantsComponent', () => {
  let component: NotificacionsVigilantsComponent;
  let fixture: ComponentFixture<NotificacionsVigilantsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NotificacionsVigilantsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificacionsVigilantsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
