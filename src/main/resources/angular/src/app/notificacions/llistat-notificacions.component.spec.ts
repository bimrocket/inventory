import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LlistatnotificacionsComponent } from './llistat-notificacions.component';

describe('LlistatnotificacionsComponent', () => {
  let component: LlistatnotificacionsComponent;
  let fixture: ComponentFixture<LlistatnotificacionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LlistatnotificacionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LlistatnotificacionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
