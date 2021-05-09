import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AreesConfiguracionsComponent } from './arees-configuracions.component';

describe('AreesConfiguracionsComponent', () => {
  let component: AreesConfiguracionsComponent;
  let fixture: ComponentFixture<AreesConfiguracionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AreesConfiguracionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AreesConfiguracionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
