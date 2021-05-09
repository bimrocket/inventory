import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogCreateUserBONoLDAPComponent } from './dialog-create-user-bono-ldap.component';

describe('DialogCreateUserBONoLDAPComponent', () => {
  let component: DialogCreateUserBONoLDAPComponent;
  let fixture: ComponentFixture<DialogCreateUserBONoLDAPComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogCreateUserBONoLDAPComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogCreateUserBONoLDAPComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
