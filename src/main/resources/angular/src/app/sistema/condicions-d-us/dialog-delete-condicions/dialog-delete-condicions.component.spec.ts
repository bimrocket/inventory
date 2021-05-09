import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogDeleteCondicionsComponent } from './dialog-delete-condicions.component';

describe('DialogDeleteCondicionsComponent', () => {
  let component: DialogDeleteCondicionsComponent;
  let fixture: ComponentFixture<DialogDeleteCondicionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogDeleteCondicionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogDeleteCondicionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
