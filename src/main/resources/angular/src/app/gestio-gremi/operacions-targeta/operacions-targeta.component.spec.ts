import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OperacionsTargetaComponent } from './operacions-targeta.component';

describe('OperacionsTargetaComponent', () => {
  let component: OperacionsTargetaComponent;
  let fixture: ComponentFixture<OperacionsTargetaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OperacionsTargetaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OperacionsTargetaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
