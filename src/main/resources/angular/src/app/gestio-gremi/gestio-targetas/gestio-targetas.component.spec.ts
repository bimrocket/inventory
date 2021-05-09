import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GestioTargetasComponent } from './gestio-targetas.component';

describe('GestioTargetasComponent', () => {
  let component: GestioTargetasComponent;
  let fixture: ComponentFixture<GestioTargetasComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GestioTargetasComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GestioTargetasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
