import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CondicionsDUsComponent } from './condicions-d-us.component';

describe('CondicionsDUsComponent', () => {
  let component: CondicionsDUsComponent;
  let fixture: ComponentFixture<CondicionsDUsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CondicionsDUsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CondicionsDUsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
