import "./featuredProperties.css";
import featuredImage1 from '../../assets/images/home/featured/featured1.webp'
import featuredImage2 from '../../assets/images/home/featured/featured2.jpeg'
import featuredImage3 from '../../assets/images/home/featured/featured3.jpeg'
import featuredImage4 from '../../assets/images/home/featured/featured4.jpeg'

const FeaturedProperties = () => {
  return (
    <div className="fp">
      <div className="fpItem">
        <img
          src={featuredImage2}
          alt=""
          className="fpImg"
        />
        <span className="fpName">Aparthotel Stare Miasto</span>
        <span className="fpCity">Madrid</span>
        <span className="fpPrice">Starting from $120</span>
        {/*<div className="fpRating">*/}
        {/*  <button>8.9</button>*/}
        {/*  <span>Excellent</span>*/}
        {/*</div>*/}
      </div>
      <div className="fpItem">
        <img
            src={featuredImage1}
          alt=""
          className="fpImg"
        />
        <span className="fpName">Comfort Suites Airport</span>
        <span className="fpCity">Austin</span>
        <span className="fpPrice">Starting from $140</span>
        {/*<div className="fpRating">*/}
        {/*  <button>9.3</button>*/}
        {/*  <span>Exceptional</span>*/}
        {/*</div>*/}
      </div>
      <div className="fpItem">
        <img
            src={featuredImage3}
          alt=""
          className="fpImg"
        />
        <span className="fpName">Four Seasons Hotel</span>
        <span className="fpCity">Lisbon</span>
        <span className="fpPrice">Starting from $99</span>
        {/*<div className="fpRating">*/}
        {/*  <button>8.8</button>*/}
        {/*  <span>Excellent</span>*/}
        {/*</div>*/}
      </div>
      <div className="fpItem">
        <img
            src={featuredImage4}
          alt=""
          className="fpImg"
        />
        <span className="fpName">Hilton Garden Inn</span>
        <span className="fpCity">Berlin</span>
        <span className="fpPrice">Starting from $105</span>
        {/*<div className="fpRating">*/}
        {/*  <button>8.9</button>*/}
        {/*  <span>Excellent</span>*/}
        {/*</div>*/}
      </div>
    </div>
  );
};

export default FeaturedProperties;
