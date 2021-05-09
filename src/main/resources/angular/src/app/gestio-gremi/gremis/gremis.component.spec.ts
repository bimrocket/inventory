import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GremisComponent } from './gremis.component';

describe('GremisComponent', () => {
  let component: GremisComponent;
  let fixture: ComponentFixture<GremisComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GremisComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GremisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
