import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CercadorOperacionsComponent } from './cercador-operacions.component';

describe('CercadorOperacionsComponent', () => {
  let component: CercadorOperacionsComponent;
  let fixture: ComponentFixture<CercadorOperacionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CercadorOperacionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CercadorOperacionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
