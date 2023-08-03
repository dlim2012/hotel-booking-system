import Navbar from "../../../../../components/navbar/Navbar";
import ProfileSidebar from "./ProfileSidebar";
import PersonalDetails from "./info/PersonalDetails";
import PersonalSecurity from "./security/PersonalSecurity";
import PersonalEmail from "./email/PersonalEmail";
import "./profileMain.css"
import {useState} from "react";
import UserSettings from "./settings/UserSettings";
import MailList from "../../../../../components/mailList/MailList";
import Footer from "../../../../../components/footer/Footer";

function ProfileMain(props) {
    const [subject, setSubject] = useState(props.subject == null ? "personal-details" : props.subject);

    var sidebarProps = {
        subject: subject,
        setSubject: setSubject
    }
    return (
        <div>
            <Navbar />
            <div className="profileContainer">
                <ProfileSidebar args={sidebarProps}/>
                {
                    subject === "personal-details" &&

                    <div className="profileContents">
                        <div className="profileTitle">
                            <h1>Personal details</h1>
                            <p>Update your info and find out how it's used.</p>
                        </div>
                        <PersonalDetails />
                    </div>
                }
                {
                    subject === "security" &&
                    <div className="profileContents">
                        <div className="profileTitle">
                            <h1>Security</h1>
                            {/*<p>Update your info and find out how it's used.</p>*/}
                        </div>
                        <PersonalSecurity />
                    </div>
                }
                {
                    subject === "email-notifications" &&
                    <div className="profileContents">
                        <div className="profileTitle">
                            <h1>Email Notifications</h1>
                            <p>Update your info and find out how it's used.</p>
                        </div>
                        <PersonalEmail />
                    </div>
                }
                {
                    subject === "settings" &&
                    <div className="profileContents">
                        <div className="profileTitle">
                            <h1>Settings</h1>
                        </div>
                        <UserSettings />
                    </div>
                }

            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default ProfileMain;