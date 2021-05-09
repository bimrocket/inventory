import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GestioOperativaComponent } from './gestio-operativa.component';

describe('GestioOperativaComponent', () => {
  let component: GestioOperativaComponent;
  let fixture: ComponentFixture<GestioOperativaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GestioOperativaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GestioOperativaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
