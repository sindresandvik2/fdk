import React from 'react';
import DatasetContactInfo from '../../../src/components/search-dataset-contactinfo';

describe('DatasetContactInfo', () => {
  let wrapper;

  beforeEach(() => {
    wrapper = shallow(<DatasetContactInfo/>);
  });

  it ('should render', () => {
    expect(wrapper).to.have.length(1);
    expect(wrapper.find('#dataset-contactinfo')).to.have.length(1);
  });

  it ('should render uri href', () => {
    wrapper.setProps({
      uri: 'http://www.example.com/some/random/uri'
    });
    expect(wrapper.find('#dataset-contact-uri').prop('href')).to.have.length(38);
  });

  it ('should render email', () => {
    wrapper.setProps({
      email: 'someone@example.com'
    });
    expect(wrapper.find('#dataset-contact-email').text()).to.have.length(19);
  });
});