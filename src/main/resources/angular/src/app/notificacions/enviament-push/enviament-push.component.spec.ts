import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EnviamentpushComponent } from './enviament-push.component';

describe('EnviamentpushComponent', () => {
  let component: EnviamentpushComponent;
  let fixture: ComponentFixture<EnviamentpushComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EnviamentpushComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnviamentpushComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
