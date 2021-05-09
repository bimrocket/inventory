import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UsuarisBoComponent } from './usuaris-bo.component';

describe('UsuarisBoComponent', () => {
  let component: UsuarisBoComponent;
  let fixture: ComponentFixture<UsuarisBoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UsuarisBoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UsuarisBoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
