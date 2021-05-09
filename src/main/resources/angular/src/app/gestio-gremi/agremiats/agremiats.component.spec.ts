import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AgremiatsComponent } from './agremiats.component';

describe('AgremiatsComponent', () => {
  let component: AgremiatsComponent;
  let fixture: ComponentFixture<AgremiatsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AgremiatsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AgremiatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
