import React from 'react';
import localization from '../../utils/localization';
import './app-footer.scss';

const Footer = () => (
  <footer className="fdk-footer">
    <div className="container">
      <div className="row">
        <div className="col-md-4">
          <p className="fdk-p-footer">
            <a href="https://www.brreg.no/personvernerklaering/">
              {localization.footer.information}
              {localization.footer.privacy}
              <i className="fa fa-external-link fdk-fa-right" />
            </a>
          </p>
        </div>
        <div className="col-md-4 text-center">
          <span className="uu-invisible" aria-hidden="false">
            Felles Datakatalog.
          </span>
          <p className="fdk-p-footer">{localization.footer.information_text}</p>
        </div>
        <div className="col-md-4 text-right">
          <p className="fdk-p-footer">
            <a href="mailto:fellesdatakatalog@brreg.no">
              <span className="uu-invisible" aria-hidden="false">
                Mailadresse.
              </span>
              {localization.footer.contact}
              {localization.footer.mail}
            </a>
          </p>
        </div>
      </div>
    </div>
  </footer>
);

export default Footer;
